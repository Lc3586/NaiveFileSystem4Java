package top.lctr.naive.file.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件服务配置
 * <p>文件修复</p>
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component("FileRepairConfig")
@ConfigurationProperties(prefix = "file.repair")
public class FileRepairConfig {
    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 线程池大小
     */
    private int threadPoolSize;

    /**
     * 定时检查需要修复的文件的时间间隔(h)
     */
    private int checkInterval;

    /**
     * 集线器配置
     */
    private HubConfig hubConfig;

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
     * 线程池大小
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * 定时检查需要修复的文件的时间间隔(m)
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * 集线器配置
     */
    public HubConfig getHubConfig() {
        return hubConfig;
    }

    public void setHubConfig(HubConfig hubConfig) {
        this.hubConfig = hubConfig;
    }
}
