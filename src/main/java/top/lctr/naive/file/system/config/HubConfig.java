package top.lctr.naive.file.system.config;

/**
 * 文件服务配置
 * <p>集线器</p>
 *
 * @author LCTR
 * @date 2022-12-08
 */
public class HubConfig {
    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 消息处理线程池大小
     */
    private int handlerThreadPoolSize;

    /**
     * 消息发送线程池大小
     */
    private int senderThreadPoolSize;

    /**
     * 是否启用
     */
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 消息处理线程池大小
     */
    public int getHandlerThreadPoolSize() {
        return handlerThreadPoolSize;
    }

    public void setHandlerThreadPoolSize(int handlerThreadPoolSize) {
        this.handlerThreadPoolSize = handlerThreadPoolSize;
    }

    /**
     * 消息发送线程池大小
     */
    public int getSenderThreadPoolSize() {
        return senderThreadPoolSize;
    }

    public void setSenderThreadPoolSize(int senderThreadPoolSize) {
        this.senderThreadPoolSize = senderThreadPoolSize;
    }
}
