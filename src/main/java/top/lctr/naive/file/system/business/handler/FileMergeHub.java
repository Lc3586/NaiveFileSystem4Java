package top.lctr.naive.file.system.business.handler;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import project.extension.ioc.IOCExtension;

/**
 * 分片文件合并集线器
 *
 * @author LCTR
 * @date 2022-12-08
 */
@ManagedService(path = "/hub/file-merge")
public class FileMergeHub {
    public FileMergeHub() {
        this.handler = IOCExtension.applicationContext.getBean(FileMergeHubHandler.class);
    }

    /**
     * 处理类
     */
    private final FileMergeHubHandler handler;

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
