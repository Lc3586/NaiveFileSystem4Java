package top.lctr.naive.file.system.dto.chunkFileDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import project.extension.mybatis.edge.annotations.EntityMappingSetting;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping
public class FunUse_ForMerge {
    /**
     * 分片文件Id
     */
    @EntityMappingSetting(self = true)
    private String id;

    /**
     * 分片索引
     */
    @EntityMappingSetting(self = true)
    private Integer index;

    /**
     * 字节数
     */
    @EntityMappingSetting(self = true)
    private Long bytes;

    /**
     * 路径
     */
    @EntityMappingSetting(self = true)
    private String path;

    /**
     * 分片文件Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 分片索引
     */
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * 字节数
     */
    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    /**
     * 路径
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
