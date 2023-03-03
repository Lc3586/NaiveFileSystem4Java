package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.mybatis.edge.annotations.MappingSetting;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiSchema;
import project.extension.openapi.model.OpenApiSchemaFormat;
import project.extension.openapi.model.OpenApiSchemaType;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 * 文件上传配置业务模型
 * <p>列表数据</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTag({"List"})
public class List extends CommonFileUploadConfig {
    @MappingSetting(ignore = true)
    private Boolean hasChildren;

    @MappingSetting(ignore = true)
    private Integer childrenCount;

    @MappingSetting(ignore = true)
    @OpenApiSchema(type = OpenApiSchemaType.model, format = OpenApiSchemaFormat.model_once)
    private java.util.List<List> children;

    /**
     * 是否拥有子级
     */
    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * 子级数量
     */
    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    /**
     * 子级
     */
    public java.util.List<List> getChildren() {
        return children;
    }

    public void setChildren(java.util.List<List> children) {
        this.children = children;
    }
}
