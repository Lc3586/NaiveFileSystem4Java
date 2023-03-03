package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiSchema;
import project.extension.openapi.model.OpenApiSchemaFormat;
import project.extension.openapi.model.OpenApiSchemaType;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 * 文件上传配置业务模型
 * <p>授权信息树状列表</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTag("Authorities")
public class AuthoritiesTree
        extends CommonFileUploadConfig {
    private Boolean authorized;

    private Boolean hasChildren;

    private Integer childrenCount;

    @OpenApiSchema(type = OpenApiSchemaType.model,
                   format = OpenApiSchemaFormat.model_once)
    private java.util.List<AuthoritiesTree> children;

    /**
     * 已授权
     */
    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

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
    public java.util.List<AuthoritiesTree> getChildren() {
        return children;
    }

    public void setChildren(java.util.List<AuthoritiesTree> children) {
        this.children = children;
    }
}
