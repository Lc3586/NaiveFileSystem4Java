package top.lctr.naive.file.system.business.handler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.standard.entity.IEntityExtension;
import project.extension.task.TaskQueueHandler;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileMergeTaskService;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileService;
import top.lctr.naive.file.system.config.UploadLargeFileConfig;
import top.lctr.naive.file.system.dto.CFMTState;
import top.lctr.naive.file.system.entity.CommonChunkFileMergeTask;
import top.lctr.naive.file.system.entityFields.CFMT_Fields;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件服务
 * <p>分片文件清理模块</p>
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class ChunkFileClearHandler
        extends TaskQueueHandler {
    public ChunkFileClearHandler(IEntityExtension entityExtension,
                                 UploadLargeFileConfig config,
                                 IChunkFileService chunkFileService,
                                 IChunkFileMergeTaskService chunkFileMergeTaskService,
                                 FileMergeHubHandler fileMergeHubHandler) {
        super("分片文件清理模块",
              config.getThreadPoolSize(),
              LoggerFactory.getLogger(ChunkFileClearHandler.class));
        this.entityExtension = entityExtension;
        this.config = config;
        this.chunkFileService = chunkFileService;
        this.chunkFileMergeTaskService = chunkFileMergeTaskService;
        this.fileMergeHubHandler = fileMergeHubHandler;
    }

    private final IEntityExtension entityExtension;

    private final UploadLargeFileConfig config;

    private final IChunkFileService chunkFileService;

    private final IChunkFileMergeTaskService chunkFileMergeTaskService;

    private final FileMergeHubHandler fileMergeHubHandler;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 启动
     */
    public void start() {
        if (!config.isEnable())
            return;

        super.start(true);

        logger.info(String.format("%s：已启动",
                                  getName()));

        //开始定时检查待清理的任务
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
     * 新增任务
     *
     * @param taskId 分片合并任务主键
     */
    public void add(String taskId)
            throws
            Exception {
        //追加至队列并处理
        super.addTask(taskId,
                      true);
    }

    /**
     * 获取用于推送的数据
     *
     * @param task      任务信息
     * @param allFields 全部字段
     * @param fields    指定字段
     * @return 数据键值对
     */
    public static Map<String, Object> getSendData(CommonChunkFileMergeTask task,
                                                  boolean allFields,
                                                  String... fields) {
        if (allFields)
            fields = new String[]{"*"};

        Map<String, Object> sendData = new HashMap<>();
        for (
                String field : fields) {
            if (field.equals("*") || field.equals(CFMT_Fields.id))
                sendData.put(CFMT_Fields.id,
                             task.getId());
            if (field.equals("*") || field.equals(CFMT_Fields.serverKey))
                sendData.put(CFMT_Fields.serverKey,
                             task.getServerKey());
            if (field.equals("*") || field.equals(CFMT_Fields.md5))
                sendData.put(CFMT_Fields.md5,
                             task.getMd5());
            if (field.equals("*") || field.equals(CFMT_Fields.name))
                sendData.put(CFMT_Fields.name,
                             task.getName());
            if (field.equals("*") || field.equals(CFMT_Fields.contentType))
                sendData.put(CFMT_Fields.contentType,
                             task.getContentType());
            if (field.equals("*") || field.equals(CFMT_Fields.extension))
                sendData.put(CFMT_Fields.extension,
                             task.getExtension());
            if (field.equals("*") || field.equals(CFMT_Fields.bytes))
                sendData.put(CFMT_Fields.bytes,
                             task.getBytes());
            if (field.equals("*") || field.equals(CFMT_Fields.size))
                sendData.put(CFMT_Fields.size,
                             task.getSize());
            if (field.equals("*") || field.equals(CFMT_Fields.specs))
                sendData.put(CFMT_Fields.specs,
                             task.getSpecs());
            if (field.equals("*") || field.equals(CFMT_Fields.total))
                sendData.put(CFMT_Fields.total,
                             task.getTotal());
            if (field.equals("*") || field.equals(CFMT_Fields.state))
                sendData.put(CFMT_Fields.state,
                             task.getState());
            if (field.equals("*") || field.equals(CFMT_Fields.info))
                sendData.put(CFMT_Fields.info,
                             task.getInfo());
            if (field.equals("*") || field.equals(CFMT_Fields.completedTime))
                sendData.put(CFMT_Fields.completedTime,
                             dateFormat.format(task.getCompletedTime()));
            if (field.equals("*") || field.equals(CFMT_Fields.createTime))
                sendData.put(CFMT_Fields.createTime,
                             dateFormat.format(task.getCreateTime()));
            if (field.equals("*") || field.equals(CFMT_Fields.updateTime))
                sendData.put(CFMT_Fields.updateTime,
                             dateFormat.format(task.getUpdateTime()));
        }

        return sendData;
    }

    /**
     * 检查待清理的任务
     */
    private void startCheck(Object state) {
        try {
            //将待清理的任务添加至队列
            chunkFileMergeTaskService.getUnclearedIdList()
                                     .forEach(x -> super.addTask(x,
                                                                 false));

            super.handler();
        } catch (Exception ex) {
            logger.error(String.format("%s：添加待清理的任务至队列失败，%s",
                                       getName(),
                                       ex.getMessage()),
                         ex);
        }

        //添加定时任务定时检查未完成的任务
        super.addScheduleTask(this::startCheck,
                              null,
                              config.getClearInterval() * 60 * 60 * 1000L);
    }

    /**
     * 处理主任务
     */
    @Override
    protected void processingTask(Object taskKey) {
        String taskKey_resolve = String.valueOf(taskKey);

        //异步执行
        super.putConcurrentTask(taskKey_resolve,
                                () -> this.handlerTask(taskKey_resolve),
                                x -> super.removeConcurrentTask(taskKey_resolve));
    }

    /**
     * 异步处理子任务
     *
     * @param id 分片文件主键
     */
    private void handlerTask(String id) {
        try {
            //获取分片合并任务信息
            CommonChunkFileMergeTask task = chunkFileMergeTaskService.get(id);

            //推送实时信息
            sendTaskInfo(task,
                         true);

            //清理所有的分片文件
            chunkFileService.clear(task.getMd5(),
                                   task.getSpecs());

            task.setState(CFMTState.已完成);
            task.setInfo("已清理全部分片文件");
            chunkFileMergeTaskService.update(entityExtension.modify(task));

            //推送实时信息
            sendTaskInfo(task,
                         false,
                         CFMT_Fields.state,
                         CFMT_Fields.info);
        } catch (Exception ex) {
            logger.error(String.format("%s：清理分片文件时异常，%s",
                                       getName(),
                                       ex.getMessage()),
                         ex);
        }
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
                 getSendData(task,
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
        if (!config.getHubConfig()
                   .isEnable())
            return;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",
                       id);
        jsonObject.put("data",
                       data);
        fileMergeHubHandler.broadcast(jsonObject.toJSONString());
    }
}
