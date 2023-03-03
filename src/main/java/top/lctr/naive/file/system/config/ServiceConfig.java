package top.lctr.naive.file.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 服务配置
 *
 * @author LCTR
 * @date 2023-03-03
 */
@Primary
@Component
@ConfigurationProperties("service")
public class ServiceConfig {
    /**
     * 服务标识
     */
    private String key;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 版本号
     */
    private String version;

    /**
     * 启用Swagger
     */
    private Boolean enableSwagger;

    /**
     * 站点资源文件根目录相对路径
     */
    private String wwwRootDirectory;

    /**
     * 服务标识
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 服务名称
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 版本号
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 启用Swagger
     */
    public Boolean getEnableSwagger() {
        return enableSwagger;
    }

    public void setEnableSwagger(Boolean enableSwagger) {
        this.enableSwagger = enableSwagger;
    }

    /**
     * 站点资源文件根目录相对路径
     */
    public String getWwwRootDirectory() {
        return wwwRootDirectory;
    }

    public void setWwwRootDirectory(String wwwRootDirectory) {
        this.wwwRootDirectory = wwwRootDirectory;
    }
}
