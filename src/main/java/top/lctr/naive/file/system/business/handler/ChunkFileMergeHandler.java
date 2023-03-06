package top.lctr.naive.file.system.business.handler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.collections.CollectionsExtension;
import project.extension.file.FileExtension;
import project.extension.number.DecimalExtension;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import project.extension.task.TaskExtension;
import project.extension.task.TaskQueueHandler;
import project.extension.tuple.Tuple2;
import project.extension.tuple.Tuple3;
import project.extension.tuple.Tuple4;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileMergeTaskService;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileService;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.config.UploadLargeFileConfig;
import top.lctr.naive.file.system.dto.CFMTState;
import top.lctr.naive.file.system.dto.FileState;
import top.lctr.naive.file.system.dto.StorageType;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_ForMerge;
import top.lctr.naive.file.system.dto.chunkFileMergeTaskDTO.ActivityInfo;
import top.lctr.naive.file.system.dto.chunkFileMergeTaskDTO.ChunksSourceInfo;
import top.lctr.naive.file.system.entity.CommonChunkFileMergeTask;
import top.lctr.naive.file.system.entityFields.CFMT_Fields;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 文件服务
 * <p>分片文件合并模块</p>
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class ChunkFileMergeHandler
        extends TaskQueueHandler {
    public ChunkFileMergeHandler(UploadLargeFileConfig config,
                                 IFileService fileService,
                                 IChunkFileService chunkFileService,
                                 IChunkFileMergeTaskService chunkFileMergeTaskService,
                                 ChunkFileClearHandler chunkFileClearHandler,
                                 FileMergeHubHandler fileMergeHubHandler,
                                 IEntityExtension entityExtension) {
        super("分片文件合并模块",
              config.getThreadPoolSize(),
              LoggerFactory.getLogger(ChunkFileMergeHandler.class));
        this.config = config;
        this.fileService = fileService;
        this.chunkFileService = chunkFileService;
        this.chunkFileMergeTaskService = chunkFileMergeTaskService;
        this.chunkFileClearHandler = chunkFileClearHandler;
        this.fileMergeHubHandler = fileMergeHubHandler;
        this.entityExtension = entityExtension;
    }

    /**
     * 全部任务
     * <p>key：任务主键, value： 任务信息</p>
     */
    private static final ConcurrentMap<String, CommonChunkFileMergeTask> stateMap = new ConcurrentHashMap<>();

    /**
     * 全部分片热度信息发送任务
     * <p>key：文件md5值+规格, value： 分片热度信息发送任务</p>
     */
    private static final ConcurrentMap<String, CompletableFuture<Void>> taskMap = new ConcurrentHashMap<>();

    private final UploadLargeFileConfig config;

    private final IFileService fileService;

    private final IChunkFileService chunkFileService;

    private final IChunkFileMergeTaskService chunkFileMergeTaskService;

    private final ChunkFileClearHandler chunkFileClearHandler;

    private final FileMergeHubHandler fileMergeHubHandler;

    private final IEntityExtension entityExtension;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 启动
     */
    public void start() {
        if (!config.isEnable())
            return;

        super.start(true,
                    () -> {
                        //同步启动清理分片文件处理类
                        chunkFileClearHandler.start();
                        return true;
                    });

        logger.info(String.format("%s：已启动",
                                  getName()));

        //开始定时检查未完成的任务
        startCheck(null);
    }

    /**
     * 关闭
     */
    @Override
    public void shutDown() {
        //同步关闭清理分片文件处理类
        chunkFileClearHandler.shutDown();

        super.shutDown();

        logger.info(String.format("%s：已关闭",
                                  getName()));
    }

    /**
     * 新增任务
     *
     * @param md5         文件MD5值
     * @param contentType 文件内容类型
     * @param extension   文件拓展名
     * @param name        文件名(不包括拓展名)
     * @param specs       分片规格
     * @param total       分片总数
     */
    public void add(String md5,
                    String contentType,
                    String extension,
                    String name,
                    int specs,
                    int total)
            throws
            Exception {
        //如任务已存在，则忽略
        if (chunkFileMergeTaskService.isAlreadyExist(md5,
                                                     specs,
                                                     total))
            return;

        CommonChunkFileMergeTask task
                = chunkFileMergeTaskService.create(md5,
                                                   contentType,
                                                   extension,
                                                   name,
                                                   specs,
                                                   total,
                                                   CFMTState.上传中);

        stateMap.put(task.getId(),
                     task);

        super.addScheduleTask((state) -> beginSendChunksSourceInfoTask(state.a,
                                                                       state.b,
                                                                       state.c,
                                                                       state.d),
                              new Tuple4<>(task.getId(),
                                           md5,
                                           specs,
                                           total),
                              1000);

        //推送实时信息
        sendTaskInfo(task,
                     true);
    }

    /**
     * 处理合并任务
     *
     * @param md5   文件md5
     * @param specs 分片规格
     * @param total 分片总数
     */
    public void handler(String md5,
                        int specs,
                        int total)
            throws
            Exception {
        //检查任务是否存在
        if (!chunkFileMergeTaskService.isAlreadyExist(md5,
                                                      specs,
                                                      total))
            throw new BusinessException("未创建分片文件合并任务信息");

        CommonChunkFileMergeTask task = chunkFileMergeTaskService.getAlreadyTask(md5,
                                                                                 specs,
                                                                                 total);

        task.setState(CFMTState.等待处理);
        chunkFileMergeTaskService.update(entityExtension.modify(task));

        //推送实时信息
        sendTaskInfo(task,
                     false,
                     CFMT_Fields.state);

        //延迟追加至队列处理
        super.addScheduleTask(state -> super.addTask(state,
                                                     true),
                              task.getId(),
                              1000);
    }

    /**
     * 检查未完成的任务
     */
    private void startCheck(Object state) {
        try {
            //将未完成的任务添加至队列
            chunkFileMergeTaskService.getUnfinishedIdList()
                                     .forEach(x -> super.addTask(x,
                                                                 false));

            super.handler();
        } catch (Exception ex) {
            logger.error(String.format("%s：添加未完成的任务至队列失败，%s",
                                       getName(),
                                       ex.getMessage()),
                         ex);
        }

        //添加定时任务定时检查未完成的任务
        super.addScheduleTask(this::startCheck,
                              null,
                              config.getCheckInterval() * 60 * 1000L);
    }

    /**
     * 尝试获取分片合并任务
     *
     * @param id 任务id
     * @return a: 任务是否存在，b: 任务信息
     */
    private Tuple2<Boolean, CommonChunkFileMergeTask> tryGetTask(String id) {
        CommonChunkFileMergeTask task = chunkFileMergeTaskService.get(id);
        if (task == null) {
            logger.warn(String.format("%s：合并分片文件失败, 指定的任务不存在[ID: %s]",
                                      getName(),
                                      id));
            return new Tuple2<>(false,
                                null);
        }
        return new Tuple2<>(true,
                            task);
    }

    /**
     * 分片合并任务是否早已完成
     *
     * @param task 任务信息
     * @return 是否已完成
     */
    private boolean isAlreadyComplete(CommonChunkFileMergeTask task) {
        FunUse_FileState state = fileService.getFileState(task.getMd5());
        if (state != null && state.getState()
                                  .equals(FileState.可用)) {
            task.setState(CFMTState.待清理);
            try {
                //文件已存在
                fileService.updateFileState(task.getMd5(),
                                            FileState.可用,
                                            state.getPath());
                task.setInfo("任务已完成.");
            } catch (Exception ex) {
                logger.error(String.format("%s：更新文件状态信息失败, 任务[ID: %s]，%s",
                                           getName(),
                                           task.getId(),
                                           ex.getMessage()),
                             ex);
                task.setInfo("任务已完成，但更新文件状态信息失败.");
            }

            updateAndSendUpdateData(task);

            return true;
        }

        //文件不存在或是正在处理
        return false;
    }

    /**
     * 获取分片文件
     *
     * @param task 任务信息
     * @return a：是否需要处理，b：全部分片文件，c：合并所需的分片文件
     */
    private Tuple3<Boolean, List<FunUse_ForMerge>, List<FunUse_ForMerge>> tryGetChunkFiles(CommonChunkFileMergeTask task) {
        //分片文件是否已全部上传
        long chunkFileCount = chunkFileService.chunkFileAlreadyCount(task.getMd5(),
                                                                     task.getSpecs());
        if (chunkFileCount != task.getTotal()) {
            //更新任务信息
            task.setInfo("分片文件还未全部上传完毕.");
            chunkFileMergeTaskService.update(entityExtension.modify(task));

            //推送实时信息
            sendTaskInfo(task,
                         false,
                         CFMT_Fields.info,
                         CFMT_Fields.updateTime);

            return new Tuple3<>(false,
                                null,
                                null);
        }

        //获取全部分片文件
        List<FunUse_ForMerge> allChunkFiles
                = chunkFileService.chunkFileAlreadyList(task.getMd5(),
                                                        task.getSpecs());

        //需要删除的分片文件Id集合
        List<String> needRemoveChunkFileIds = new ArrayList<>();

        allChunkFiles = allChunkFiles.stream()
                                     .filter(x -> {
                                         if (!new File(chunkFileService.getFilePath(x.getPath())).exists()) {
                                             needRemoveChunkFileIds.add(x.getId());
                                             return false;
                                         }
                                         return true;
                                     })
                                     .collect(Collectors.toList());

        //删除已损坏的分片文件
        if (CollectionsExtension.anyPlus(needRemoveChunkFileIds))
            chunkFileService.delete(needRemoveChunkFileIds);

        //分片文件按索引分组
        Map<Integer, List<FunUse_ForMerge>> allChunkFileGroupMap
                = allChunkFiles.stream()
                               .collect(Collectors.groupingBy(FunUse_ForMerge::getIndex));

        //分片文件缺失，无法合并
        if (allChunkFileGroupMap.keySet()
                                .size() != task.getTotal()) {
            //更新任务信息
            task.setState(CFMTState.失败);
            task.setInfo("合并分片文件失败, 部分分片文件已损坏.");
            updateAndSendUpdateData(task);

            return new Tuple3<>(false,
                                null,
                                null);
        }

        //获取合并任务所需的分片文件
        List<FunUse_ForMerge> needChunkFiles = new ArrayList<>();
        for (int i = 0; i < task.getTotal(); i++)
            needChunkFiles.add(allChunkFileGroupMap.get(i)
                                                   .get(0));

        return new Tuple3<>(true,
                            allChunkFiles,
                            needChunkFiles);
    }

    /**
     * 合并分片文件
     *
     * @param task           任务信息
     * @param needChunkFiles 合并所需的分片文件
     */
    private void merge(CommonChunkFileMergeTask task,
                       List<FunUse_ForMerge> needChunkFiles) {
        //更新任务信息
        task.setState(CFMTState.处理中);
        task.setInfo("分片文件合并中.");
        updateAndSendUpdateData(task);

        //文件夹相对路径
        String dirPath = Paths.get("upload",
                                   new SimpleDateFormat("yyyy-MM-dd").format(
                                           new Date()))
                              .toString();

        //存储的文件夹
        File dir = new File(Paths.get(fileService.getWWWRootDirectory(),
                                      dirPath)
                                 .toString());

        if (!dir.exists() && !dir.mkdirs())
            throw new BusinessException("创建文件目录失败");

        //数据库中存储的文件相对路径
        String dbPath = Paths.get(dirPath,
                                  String.format("%s%s",
                                                task.getName(),
                                                task.getExtension()))
                             .toString();

        //存储的文件
        File file = new File(Paths.get(fileService.getWWWRootDirectory(),
                                       dbPath)
                                  .toString());

        try (FileChannel toChannel = new RandomAccessFile(file,
                                                          "rws").getChannel()) {
            for (FunUse_ForMerge chunkFile : needChunkFiles) {
                if (task.getCurrentChunkIndex() >= chunkFile.getIndex()) {
                    toChannel.position(toChannel.position() + chunkFile.getBytes());
                    continue;
                }

                try (FileChannel fromChannel = new RandomAccessFile(new File(chunkFileService.getFilePath(chunkFile.getPath())),
                                                                    "rw").getChannel()) {
                    toChannel.transferFrom(fromChannel,
                                           task.getBytes(),
                                           chunkFile.getBytes());
                } catch (Exception ex) {
                    throw new BusinessException("写入文件数据失败",
                                                ex);
                }

                //更新任务信息
                task.setCurrentChunkIndex(chunkFile.getIndex());
                task.setBytes(task.getBytes() + chunkFile.getBytes());
                chunkFileMergeTaskService.update(entityExtension.modify(task));

                //推送实时信息
                sendTaskInfo(task,
                             false,
                             CFMT_Fields.currentChunkIndex,
                             CFMT_Fields.bytes,
                             CFMT_Fields.updateTime);
            }
        } catch (Exception ex) {
            throw new BusinessException("合并文件失败",
                                        ex);
        }

        task.setSize(FileExtension.getFileSize(task.getBytes()));
        task.setPath(dbPath);
    }

    /**
     * 合并完成后更新信息
     *
     * @param task 任务信息
     * @return 是否成功
     */
    private boolean completed(CommonChunkFileMergeTask task) {
        task.setState(CFMTState.待清理);
        task.setCompletedTime(new Date());
        chunkFileMergeTaskService.update(entityExtension.modify(task));

        try {
            fileService.update(task.getMd5(),
                               task.getName(),
                               task.getExtension(),
                               task.getContentType(),
                               task.getBytes(),
                               task.getPath(),
                               StorageType.相对路径,
                               FileState.可用,
                               true);

            //更新任务信息
            task.setInfo("已合并所有分片文件.");
            updateAndSendUpdateData(task);
            return true;
        } catch (Exception ex) {
            logger.error(String.format("%s：更新文件信息失败, 任务[ID: %s]，%s",
                                       getName(),
                                       task.getId(),
                                       ex.getMessage()),
                         ex);

            //更新任务信息
            task.setInfo("合并分片文件成功, 但更新文件信息失败.");
            updateAndSendUpdateData(task);

            return false;
        }
    }

    /**
     * 处理主任务
     */
    @Override
    protected void processingTask(Object taskKey) {
        String taskKey_resolve = String.valueOf(taskKey);

        CommonChunkFileMergeTask task = stateMap.get(taskKey_resolve);
        if (task == null) return;

        //判断设备是否已被占用
        if (super.containsConcurrentTask(task.getMd5())) {
            //延时添加至队列，等待下次处理
            super.addScheduleTask(super::addTask,
                                  taskKey,
                                  1000L);
            return;
        }

        //异步执行
        super.putConcurrentTask(taskKey_resolve,
                                () -> this.handlerTask(task),
                                x -> super.removeConcurrentTask(task.getMd5()));
    }

    /**
     * 异步处理子任务
     *
     * @param task 任务信息
     */
    private void handlerTask(CommonChunkFileMergeTask task) {
        try {
            AtomicReference<CommonChunkFileMergeTask> arTask = new AtomicReference<>();
            arTask.set(task);

//            //所有操作均在事务下执行
//            Tuple2<Boolean, Exception> result = RepositoryExtension.runTransaction(() -> {
            //判断任务信息是否已失效
            if (chunkFileMergeTaskService.isExpire(arTask.get()
                                                         .getId()))
                return;

            //任务是否早已完成
            if (isAlreadyComplete(arTask.get())) {
                //完成后更新数据
                if (!completed(arTask.get()))
                    //添加定时任务等待一段时间后再次尝试
                    super.addScheduleTask(this::addTask,
                                          arTask.get()
                                                .getId(),
                                          10000L);
                return;
            }

            //尝试获取分片文件信息
            Tuple3<Boolean, List<FunUse_ForMerge>, List<FunUse_ForMerge>> tryGetChunkFilesResult
                    = tryGetChunkFiles(arTask.get());
            if (!tryGetChunkFilesResult.a)
                return;

            //合并所需的分片文件
            List<FunUse_ForMerge> needChunkFiles = tryGetChunkFilesResult.c;

            //合并文件
            merge(arTask.get(),
                  needChunkFiles);

            //完成后更新数据
            if (!completed(arTask.get()))
                //添加定时任务等待一段时间后再次尝试
                super.addScheduleTask(this::addTask,
                                      arTask.get()
                                            .getId(),
                                      10000L);
//            });
//
//            if (!result.a)
//                throw result.b;

            //取消分片热度信息推送任务
            cancelSendChunksSourceInfoTask(arTask.get()
                                                 .getId());
            //推送上传结束后的分片热度信息
            sendChunksSourceInfo(arTask.get()
                                       .getId(),
                                 arTask.get()
                                       .getMd5(),
                                 arTask.get()
                                       .getSpecs(),
                                 arTask.get()
                                       .getTotal()
            );

            stateMap.remove(arTask.get()
                                  .getId());

            //清理分片文件
            if (arTask.get()
                      .getState()
                      .equals(CFMTState.待清理))
                chunkFileClearHandler.add(arTask.get()
                                                .getId());
        } catch (Exception ex) {
            String error = "处理分片文件合并任务时异常";

            logger.error(String.format("%s：%s，%s",
                                       getName(),
                                       error,
                                       ex.getMessage()),
                         ex);

            //推送实时信息
            sendTaskInfo(task,
                         false,
                         CFMT_Fields.info,
                         CFMT_Fields.state);
            Map<String, Object> data = new HashMap<>();
            data.put("exception",
                     ex.toString());
            sendData(task.getId(),
                     data);

            //添加定时任务等待一段时间后再次尝试
            super.addScheduleTask(this::addTask,
                                  task.getId(),
                                  10000L);
        }
    }

    /**
     * 更新并发送更新数据
     *
     * @param task 合并任务
     */
    private void updateAndSendUpdateData(CommonChunkFileMergeTask task) {
        chunkFileMergeTaskService.update(entityExtension.modify(task));

        //推送实时信息
        sendTaskInfo(task,
                     false,
                     CFMT_Fields.state,
                     CFMT_Fields.info,
                     CFMT_Fields.updateTime,
                     CFMT_Fields.completedTime);
    }

    /**
     * 获取分片热度信息
     *
     * @param total            总数
     * @param chunkFileIndices 分片索引对应数量集合
     * @return 分片热度信息
     */
    private static List<ActivityInfo> getActivityInfo(int total,
                                                      Map<Integer, Integer> chunkFileIndices) {
        List<ActivityInfo> activities = new ArrayList<>();
        int lastActivity = 0;
        for (int i = 0; i < total; i++) {
            //索引对应的分片数量
            int activity = chunkFileIndices.getOrDefault(i,
                                                         0);

            if (lastActivity != activity) {
                //添加热度信息
                activities.add(new ActivityInfo(activity,
                                                1d));

                lastActivity = activity;
            } else {
                if (activities.size() == 0)
                    activities.add(new ActivityInfo(activity,
                                                    1d));
                else {
                    ActivityInfo last = activities.get(activities.size() - 1);
                    last.setPercentage(last.getPercentage() + 1);
                }
            }
        }

        activities.forEach(x -> x.setPercentage(DecimalExtension.round(x.getPercentage() / total,
                                                                       4)
                                                                .doubleValue() * 100));

        return activities;
    }

    /**
     * 发送分片文件来源信息
     * <p>主动发送一次信息</p>
     *
     * @param id    任务主键
     * @param md5   文件md5
     * @param specs 分片规格
     * @param total 分片总数
     */
    private void sendChunksSourceInfo(String id,
                                      String md5,
                                      int specs,
                                      int total) {
        ChunksSourceInfo chunksSourceInfo = new ChunksSourceInfo(md5,
                                                                 specs,
                                                                 total);

        Map<Integer, Integer> chunkFileIndices = new HashMap<>();

        chunkFileService.chunkFileIndicesList(md5,
                                              specs)
                        .forEach(x -> chunkFileIndices
                                .put(x.getIndex(),
                                     chunkFileIndices
                                             .getOrDefault(x.getIndex(),
                                                           0)
                                             + x.getCount()));


        chunksSourceInfo.setActivities(getActivityInfo(total,
                                                       chunkFileIndices));

        Map<String, Object> data = new HashMap<>();
        data.put("chunksSourceInfo",
                 chunksSourceInfo);
        sendData(id,
                 data);
    }

    /**
     * 发送分片来源信息任务
     * <p>在任务结束前一直发送最新的信息</p>
     *
     * @param id    任务主键
     * @param md5   文件md5
     * @param specs 分片规格
     * @param total 分片总数
     * @return 发送分片来源信息任务
     */
    private CompletableFuture<Void> sendChunksSourceInfoTask(String id,
                                                             String md5,
                                                             int specs,
                                                             int total) {
        return CompletableFuture.runAsync(() -> {
            try {
                Date lastChunkFileUpload = null;
                int lastTaskCurrentChunkIndex = -2;

                while (chunkFileMergeTaskService.isUploading(md5,
                                                             specs)) {
                    Date _lastChunkFileUpload
                            = chunkFileService.lastUploadedChunkFileCreateTime(md5,
                                                                               specs);

                    if (Objects.equals(lastChunkFileUpload,
                                       _lastChunkFileUpload)) {
                        int _lastTaskCurrentChunkIndex
                                = chunkFileMergeTaskService.lastCurrentIndex(md5,
                                                                             specs);

                        if (lastTaskCurrentChunkIndex == _lastTaskCurrentChunkIndex) {
                            TaskExtension.delay(500);
                            continue;
                        } else
                            lastTaskCurrentChunkIndex = _lastTaskCurrentChunkIndex;
                    } else
                        lastChunkFileUpload = _lastChunkFileUpload;

                    sendChunksSourceInfo(id,
                                         md5,
                                         specs,
                                         total);

                    TaskExtension.delay(500);
                }
            } catch (Exception ex) {
                logger.error(String.format("%s：发送分片来源信息任务发生异常, 任务[ID: %s]，%s",
                                           getName(),
                                           id,
                                           ex.getMessage()),
                             ex);
            }
        });
    }

    /**
     * 开始发送分片信息任务
     *
     * @param id    任务主键
     * @param md5   文件md5
     * @param specs 分片规格
     * @param total 分片总数
     */
    private void beginSendChunksSourceInfoTask(String id,
                                               String md5,
                                               int specs,
                                               int total) {
        CompletableFuture<Void> task = sendChunksSourceInfoTask(id,
                                                                md5,
                                                                specs,
                                                                total);

        //更新至任务集合中
        taskMap.put(id,
                    task);
    }

    /**
     * 取消发送分片信息任务
     *
     * @param id 任务主键
     */
    private static void cancelSendChunksSourceInfoTask(String id) {
        CompletableFuture<Void> task = taskMap.get(id);

        if (task != null && !task.isDone())
            task.cancel(true);
    }

    /**
     * 发送任务信息
     *
     * @param task      设备信息
     * @param allFields 全部字段
     * @param fields    指定字段
     */
    private void sendTaskInfo(CommonChunkFileMergeTask task,
                              boolean allFields,
                              String... fields) {
        if (!config.getHubConfig()
                   .isEnable())
            return;

        sendData(task.getId(),
                 ChunkFileClearHandler.getSendData(task,
                                                   allFields,
                                                   fields));
    }

    /**
     * 发送数据
     *
     * @param id   任务Id
     * @param data 数据键值对
     */
    private void sendData(String id,
                          Map<String, Object> data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",
                       id);
        jsonObject.put("data",
                       data);
        fileMergeHubHandler.broadcast(jsonObject.toJSONString());
    }
}
