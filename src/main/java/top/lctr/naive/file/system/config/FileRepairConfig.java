package top.lctr.naive.file.system.config;

import lombok.Data;
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
@Data
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
}
