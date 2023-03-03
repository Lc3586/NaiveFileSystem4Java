package top.lctr.naive.file.system.dto.fileDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFile;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFile.class)
public class DeleteFunUse_File {
    /**
     * Id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件上传配置Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 名称
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件扩展名
     */
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * 存储类型
     */
    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    /**
     * 文件路径
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
