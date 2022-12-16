package top.lctr.naive.file.system.business.handler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.standard.entity.IEntityExtension;
import project.extension.task.TaskQueueHandler;
import project.extension.task.ActionTimerTask;
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
 * <p>清理分片文件处理类</p>
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component("ChunkFileClearHandler")
public class ChunkFileClearHandler
        extends TaskQueueHandler {
    public ChunkFileClearHandler(IEntityExtension entityExtension,
                                 UploadLargeFileConfig config,
                                 IChunkFileService chunkFileService,
                                 IChunkFileMergeTaskService chunkFileMergeTaskService,
                                 FileMergeHubHandler fileMergeHubHandler) {
        super("清理分片文件处理类",
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

    /**
     * 启动
     */
    @Override
    public void start() {
        if (!config.isEnable())
            return;

        super.start();

        startCheck(null);
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
     * 检查待清理的任务
     */
    private void startCheck(Object state) {
        try {
            //将待清理的任务添加至队列
            chunkFileMergeTaskService.getUnclearedIdList(false)
                                     .forEach(x -> super.addTask(x,
                                                                 false));

            super.handler();
        } catch (Exception ex) {
            logger.warn("添加待清理的任务至队列失败",
                        ex);
        }

        //添加定时任务定时检查未完成的任务
        super.addScheduleTask(new ActionTimerTask<>(this::startCheck,
                                                    null),
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
            CommonChunkFileMergeTask task = chunkFileMergeTaskService.get(id,
                                                                          false);

            //推送实时信息
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, Object> sendData = new HashMap<>();
            sendData.put(CFMT_Fields.id,
                         task.getId());
            sendData.put(CFMT_Fields.serverKey,
                         task.getServerKey());
            sendData.put(CFMT_Fields.md5,
                         task.getMd5());
            sendData.put(CFMT_Fields.name,
                         task.getName());
            sendData.put(CFMT_Fields.contentType,
                         task.getContentType());
            sendData.put(CFMT_Fields.extension,
                         task.getExtension());
            sendData.put(CFMT_Fields.bytes,
                         task.getBytes());
            sendData.put(CFMT_Fields.size,
                         task.getSize());
            sendData.put(CFMT_Fields.specs,
                         task.getSpecs());
            sendData.put(CFMT_Fields.total,
                         task.getTotal());
            sendData.put(CFMT_Fields.state,
                         task.getState());
            sendData.put(CFMT_Fields.info,
                         task.getInfo());
            sendData.put(CFMT_Fields.createTime,
                         dateFormat.format(task.getCreateTime()));
            sendData.put(CFMT_Fields.updateTime,
                         dateFormat.format(task.getUpdateTime()));
            sendData.put(CFMT_Fields.completedTime,
                         dateFormat.format(task.getCompletedTime()));
            sendUpdateData(task.getId(),
                           sendData);

            //清理所有的分片文件
            chunkFileService.clear(task.getMd5(),
                                   task.getSpecs(),
                                   false);

            task.setState(CFMTState.已完成);
            task.setInfo("已清理全部分片文件");
            chunkFileMergeTaskService.update(entityExtension.modify(task),
                                             false);

            //推送实时信息
            sendData.clear();
            sendData.put(CFMT_Fields.state,
                         CFMTState.已完成);
            sendData.put(CFMT_Fields.info,
                         task.getInfo());
            sendUpdateData(task.getId(),
                           sendData);
        } catch (Exception ex) {
            logger.error("清理分片文件时异常",
                         ex);
        }
    }

    /**
     * 发送更新数据
     *
     * @param id   任务Id
     * @param data 更新的数据
     */
    private void sendUpdateData(String id,
                                Map<String, Object> data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",
                       id);
        jsonObject.put("data",
                       data);
        fileMergeHubHandler.broadcast(jsonObject.toJSONString());
    }
}
