package top.lctr.naive.file.system.hub;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import project.extension.ioc.IOCExtension;
import top.lctr.naive.file.system.business.handler.FileMergeHubHandler;
import top.lctr.naive.file.system.config.UploadLargeFileConfig;

/**
 * 分片文件合并集线器
 *
 * @author LCTR
 * @date 2023-03-03
 */
@ManagedService(path = "/hub/file-merge")
public class FileMergeHub {
    public FileMergeHub() {
        this.handler = IOCExtension.applicationContext.getBean(FileMergeHubHandler.class);
        this.config = IOCExtension.applicationContext.getBean(UploadLargeFileConfig.class);
    }

    /**
     * 处理类
     */
    private final FileMergeHubHandler handler;

    /**
     * 配置
     */
    private final UploadLargeFileConfig config;

    /**
     * 客户端
     */
    private AtmosphereResource client;

    /**
     * 连接已就绪
     *
     * @param client 客户端
     */
    @Ready
    public void onReady(final AtmosphereResource client) {
        if (!config.isEnable()
                || !config.getHubConfig()
                          .isEnable()) {
            try {
                client.close();
                return;
            } catch (Exception ignore) {

            }
        }

        this.client = client;
        handler.add(client);
    }

    /**
     * 连接已关闭
     *
     * @param event 参数
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        this.client = null;
        handler.remove(event.getResource()
                            .uuid());
    }

    /**
     * 接收消息
     *
     * @param message 消息
     * @return 要发送的消息，为null或空时不发送
     */
    @org.atmosphere.config.service.Message
    public String onMessage(String message) {
        handler.handlerMessage(client.uuid(),
                               message);
        return null;
    }
}
