package top.lctr.naive.file.system.business.handler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import project.extension.Identity.SnowFlake;
import project.extension.file.FileExtension;
import project.extension.file.ImageHelper;
import project.extension.file.PathExtension;
import project.extension.standard.entity.IEntityExtension;
import project.extension.task.TaskQueueHandler;
import project.extension.tuple.Tuple2;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.business.service.Interface.IPersonalFileService;
import top.lctr.naive.file.system.config.FileRepairConfig;
import top.lctr.naive.file.system.dto.FileState;
import top.lctr.naive.file.system.dto.FileType;
import top.lctr.naive.file.system.dto.PersonalFileState;
import top.lctr.naive.file.system.dto.StorageType;
import top.lctr.naive.file.system.entity.common.CommonFile;
import top.lctr.naive.file.system.entityFields.F_Fields;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件信息修复模块
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class FileRepairHandler
        extends TaskQueueHandler {
    public FileRepairHandler(FileRepairConfig config,
                             IFileService fileService,
                             IPersonalFileService personalFileService,
                             FileRepairHubHandler fileRepairHubHandler,
                             IEntityExtension entityExtension) {
        super("文件信息修复模块",
              config.getThreadPoolSize(),
              LoggerFactory.getLogger(FileRepairHandler.class));
        this.config = config;
        this.fileService = fileService;
        this.personalFileService = personalFileService;
        this.fileRepairHubHandler = fileRepairHubHandler;
        this.entityExtension = entityExtension;
    }

    private final FileRepairConfig config;

    private final IFileService fileService;

    private final IPersonalFileService personalFileService;

    private final FileRepairHubHandler fileRepairHubHandler;

    private final IEntityExtension entityExtension;

    /**
     * 启动
     */
    public void start() {
        if (!config.isEnable())
            return;

        super.start(true);

        logger.info(String.format("%s：已启动",
                                  getName()));

        //开始定时检查待修复的文件
        startCheck(null);
    }

    /**
     * 关闭
     */
    @Override
    public void shutDown() {
        super.shutDown();

        logger.info(String.format("%s：已关闭",
                                  getName()));
    }

    /**
     * 新增文件
     *
     * @param fileId 文件主键
     */
    public void add(String fileId)
            throws
            Exception {
        //推送实时信息
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("info",
                     "待修复");
        sendData(fileId,
                 sendData);

        //追加至队列并处理
        super.addTask(fileId,
                      true);
    }

    /**
     * 检查待修复的文件
     */
    private void startCheck(Object state) {
        try {
            //将待修复的任务添加至队列
            fileService.getUnrepairedIdList()
                       .forEach(x -> super.addTask(x,
                                                   false));

            super.handler();
        } catch (Exception ex) {
            logger.error(String.format("%s：添加待修复的文件至队列失败，%s",
                                       getName(),
                                       ex.getMessage()),
                         ex);
        }

        //添加定时任务定时检查待修复的文件
        super.addScheduleTask(this::startCheck,
                              null,
                              config.getCheckInterval() * 60 * 60 * 1000L);
    }

    /**
     * 处理主任务
     */
    @Override
    protected void processingTask(Object fileId) {
        String fileId_resolve = String.valueOf(fileId);

        //异步执行
        super.putConcurrentTask(fileId_resolve,
                                () -> this.handlerTask(fileId_resolve),
                                x -> super.removeConcurrentTask(fileId_resolve));
    }

    /**
     * 异步处理子任务
     *
     * @param id 文件主键
     */
    private void handlerTask(String id) {
        Map<String, Object> sendData = new HashMap<>();

        try {
            //尝试获取文件信息
            Tuple2<Boolean, CommonFile> tryGetFileInfoResult
                    = tryGetFileInfo(id);

            if (!tryGetFileInfoResult.a) {
                //推送实时信息
                sendData.put("info",
                             "文件不存在");
                sendData.put("error",
                             true);
                sendData(id,
                         sendData);
                return;
            }

            CommonFile fileInfo = tryGetFileInfoResult.b;

            //文件是否早已修复
            if (isAlreadyFixed(fileInfo)) {
                //推送实时信息
                sendData.put("info",
                             "已修复");
                sendData.put("success",
                             true);
                sendData(id,
                         sendData);
                return;
            }

            //修复
            if (!repair(fileInfo)) {
                fileInfo.setState(FileState.修复失败);
//                    continue;

                //推送实时信息
                sendData.put("info",
                             "修复失败");
                sendData.put("error",
                             true);
                sendData(id,
                         sendData);
            }

            //修复结束后更新数据
            if (completed(fileInfo)) {
                sendData.put("info",
                             "相关的个人文件信息已全部更新");
            } else {
                sendData.put("info",
                             "更新个人文件信息失败");
            }

            //推送实时信息
            sendData(id,
                     sendData);
        } catch (Exception ex) {
            String error = "修复文件信息时异常";

            logger.error(String.format("%s：%s，%s",
                                       getName(),
                                       error,
                                       ex.getMessage()),
                         ex);

            //推送实时信息
            sendData.put("info",
                         error);
            sendData.put("exception",
                         ex.toString());
            sendData.put("error",
                         true);
            sendData(id,
                     sendData);
        }
    }

    /**
     * 尝试获取文件信息
     *
     * @param id 任务id
     * @return a: 任务是否存在，b: 任务信息
     */
    private Tuple2<Boolean, CommonFile> tryGetFileInfo(String id) {
        CommonFile file = fileService.get(id);
        if (file == null) {
            logger.warn(String.format("%s：修复文件信息失败, 指定的文件信息不存在[ID: %s]",
                                      getName(),
                                      id));
            return new Tuple2<>(false,
                                null);
        }
        return new Tuple2<>(true,
                            file);
    }

    /**
     * 文件信息是否早已修复
     *
     * @param file 文件信息
     * @return 是否已修复
     */
    private boolean isAlreadyFixed(CommonFile file) {
        return !file.getState()
                    .equals(FileState.待修复);
    }

    /**
     * 修复文件信息
     *
     * @param fileInfo 文件信息
     */
    private boolean repair(CommonFile fileInfo)
            throws
            Exception {
        //推送实时信息
        Map<String, Object> sendData = new HashMap<>();
        sendData.put(F_Fields.state,
                     fileInfo.getState());
        sendData.put("info",
                     "开始修复");
        sendData(fileInfo.getId(),
                 sendData);

        //存储的文件
        File file = new File(fileService.getFilePath(fileInfo.getPath()));

        if (file.exists()) {
            //推送实时信息
            sendData = new HashMap<>();
            sendData.put("info",
                         "修复基础信息");
            sendData(fileInfo.getId(),
                     sendData);

            //处理Base64编码的图片
            if (fileInfo.getStorageType()
                        .equals(StorageType.Base64)) {
                fileInfo.setStorageType(StorageType.相对路径);
                fileInfo.setName(new SnowFlake(1,
                                               1).nextId2String());
                fileInfo.setExtension(".jpg");
                //在数据库中存储相对路径
                String relativePath = Paths.get("upload",
                                                new SimpleDateFormat("yyyy-MM").format(
                                                        new Date()),
                                                String.format("%s%s",
                                                              fileInfo.getName(),
                                                              fileInfo.getExtension()))
                                           .toString();

                //文件存储的绝对路径
                String path = Paths.get(fileService.getWWWRootDirectory(),
                                        relativePath)
                                   .toString();
                Tuple2<InputStream, Integer> image = ImageHelper.getBase64Image2IO(fileInfo.getPath());
                FileExtension.save(image.a,
                                   path,
                                   Long.parseLong(Integer.toString(image.b)));
            }

            //文件拓展名
            if (!StringUtils.hasText(fileInfo.getExtension()))
                fileInfo.setExtension(PathExtension.getExtension(file.getName()));

            //文件名
            if (!StringUtils.hasText(fileInfo.getName()))
                fileInfo.setName(PathExtension.trimExtension(file.getName(),
                                                             fileInfo.getExtension()));

            //文件内容类型
            if (!StringUtils.hasText(fileInfo.getContentType()))
                fileInfo.setContentType(Files.probeContentType(file.toPath()));

            //文件类型
            if (fileInfo.getFileType()
                        .equals(FileType.未知))
                fileInfo.setFileType(FileType.getFileTypeByMIME(fileInfo.getContentType()));

            //文件字节数
            if (fileInfo.getBytes() == null || fileInfo.getBytes() == 0)
                fileInfo.setBytes(file.length());

            //文件大小
            if (!StringUtils.hasText(fileInfo.getSize()))
                fileInfo.setSize(FileExtension.getFileSize(file.length()));

            //推送实时信息
            sendData = new HashMap<>();
            sendData.put("name",
                         fileInfo.getName());
            sendData.put("extension",
                         fileInfo.getExtension());
            sendData.put("contentType",
                         fileInfo.getContentType());
            sendData.put("fileType",
                         fileInfo.getFileType());
            sendData.put("bytes",
                         fileInfo.getBytes());
            sendData.put("size",
                         fileInfo.getSize());
            sendData.put("info",
                         "计算MD5值");
            sendData(fileInfo.getId(),
                     sendData);

            //MD5校验
            if (!StringUtils.hasText(fileInfo.getMd5()))
                fileInfo.setMd5(FileExtension.md5(file));

            //状态
            fileInfo.setState(FileState.可用);

            //推送实时信息
            sendData = new HashMap<>();
            sendData.put(F_Fields.state,
                         fileInfo.getState());
            sendData.put("md5",
                         fileInfo.getMd5());
            sendData.put("info",
                         "修复成功");
            sendData(fileInfo.getId(),
                     sendData);

            return true;
        } else {
//            fileInfo.setState(FileState.已删除);
            logger.warn(String.format("%s：修复文件信息失败, 文件[ID: %s]不存在，等待下次修复",
                                      getName(),
                                      fileInfo.getId()));

            //推送实时信息
            sendData = new HashMap<>();
            sendData.put(F_Fields.state,
                         fileInfo.getState());
            sendData.put("info",
                         "文件不存在，修复失败");
            sendData(fileInfo.getId(),
                     sendData);

            return false;
        }
    }

    /**
     * 修复结束后更新信息
     *
     * @param fileInfo 文件信息
     * @return 是否成功
     */
    private boolean completed(CommonFile fileInfo) {
        //更新文件信息
        fileService.update(entityExtension.modify(fileInfo));

        try {
            personalFileService.changeState(fileInfo.getId(),
                                            PersonalFileState.待修复,
                                            fileInfo.getState()
                                                    .equals(FileState.修复失败)
                                            ? PersonalFileState.修复失败
                                            : PersonalFileState.可用);
            return true;
        } catch (Exception ex) {
            logger.error(String.format("%s：更新个人文件信息失败, 文件信息[ID: %s]，%s",
                                       getName(),
                                       fileInfo.getId(),
                                       ex.getMessage()),
                         ex);
            return false;
        }
    }

    /**
     * 发送数据
     *
     * @param id   任务Id
     * @param data 数据键值对
     */
    private void sendData(String id,
                          Map<String, Object> data) {
        if (!config.getHubConfig()
                   .isEnable())
            return;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",
                       id);
        jsonObject.put("data",
                       data);
        fileRepairHubHandler.broadcast(jsonObject.toJSONString());
    }
}
