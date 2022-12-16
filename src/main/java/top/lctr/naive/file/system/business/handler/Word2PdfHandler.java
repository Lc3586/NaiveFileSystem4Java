package top.lctr.naive.file.system.business.handler;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.extension.task.ActionTimerTask;
import project.extension.task.TaskQueueHandler;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.config.Word2PdfConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * Word文件自动转换Pdf文件处理类
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component("Word2PdfHandler")
public class Word2PdfHandler
        extends TaskQueueHandler {
    public Word2PdfHandler(Word2PdfConfig config,
                           IFileService fileService,
                           Word2PdfHubHandler word2PdfHubHandler) {
        super("Word文件自动转换Pdf文件处理类",
              config.getThreadPoolSize(),
              LoggerFactory.getLogger(Word2PdfHandler.class));
        this.config = config;
        this.fileService = fileService;
        this.word2PdfHubHandler = word2PdfHubHandler;
    }

    private final Word2PdfConfig config;

    /**
     * 服务器标识
     */
    @Value("${file.serverKey}")
    private String serverKey;

    private final IFileService fileService;

    private final Word2PdfHubHandler word2PdfHubHandler;

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
     * 新增文件
     *
     * @param fileId 文件主键
     */
    public void add(String fileId)
            throws
            Exception {
        if (!config.isEnable())
            return;

        //推送实时信息
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("info",
                     "待转换");
        sendUpdateData(fileId,
                       sendData);

        //追加至队列并处理
        super.addTask(fileId,
                      true);
    }

    /**
     * 检查待转换为pdf的文件
     */
    private void startCheck(Object state) {
        try {
            //将待修复的任务添加至队列
            fileService.getUnConvert2PdfIdList()
                       .forEach(x -> super.addTask(x,
                                                   false));

            super.handler();
        } catch (Exception ex) {
            logger.warn("添加待转换为pdf的文件至队列失败",
                        ex);
        }

        //添加定时任务定时检查待修复的文件
        super.addScheduleTask(new ActionTimerTask<>(this::startCheck,
                                                    null),
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
            //转换
            StopWatch watch = new StopWatch();
            watch.start();
            fileService.word2Pdf(id);
            watch.stop();

            //推送实时信息
            sendData.put("info",
                         String.format("转换成功，耗时: %sms",
                                       watch.getTime()));
            sendUpdateData(id,
                           sendData);
        } catch (Exception ex) {
            String error = "转换文件时异常";

            logger.error(error,
                         ex);

            //推送实时信息
            sendData.put("info",
                         error);
            sendData.put("exception",
                         ex.toString());
            sendData.put("error",
                         true);
            sendUpdateData(id,
                           sendData);
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
        word2PdfHubHandler.broadcast(jsonObject.toJSONString());
    }
}
