package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFileUploadConfig.class)
public class DragSortFunUse_Target {
    /**
     * 文件上传配置Id
     */
    private String id;

    /**
     * 根Id
     */
    private String rootId;

    /**
     * 父Id
     */
    private String parentId;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 文件上传配置Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getRootId() {
        return rootId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    /**
     * 排序值
     */
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
