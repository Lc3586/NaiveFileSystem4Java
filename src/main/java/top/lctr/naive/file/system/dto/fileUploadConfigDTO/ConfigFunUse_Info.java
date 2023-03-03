package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFileUploadConfig.class)
public class ConfigFunUse_Info {
    /**
     * 文件上传配置Id
     */
    private String id;

    /**
     * 启用
     */
    private Boolean enable;

    /**
     * 文件数量下限
     */
    private Integer lowerLimit;

    /**
     * 文件数量上限
     */
    private Integer upperLimit;

    /**
     * 允许的MIME类型
     */
    private String allowedTypes;

    /**
     * 禁止的MIME类型
     */
    private String prohibitedTypes;

    /**
     * 说明
     */
    private String explain;

//    /**
//     * 引用的上传配置Id
//     */
//    private String referenceId;
//
//    /**
//     * 级联引用
//     */
//    private Boolean referenceTree;

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
     * 启用
     */
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * 文件数量下限
     */
    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * 文件数量上限
     */
    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * 允许的MIME类型
     */
    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    /**
     * 禁止的MIME类型
     */
    public String getProhibitedTypes() {
        return prohibitedTypes;
    }

    public void setProhibitedTypes(String prohibitedTypes) {
        this.prohibitedTypes = prohibitedTypes;
    }

    /**
     * 说明
     */
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

//    /**
//     * 引用的上传配置Id
//     */
//    public String getReferenceId() {
//        return referenceId;
//    }
//
//    public void setReferenceId(String referenceId) {
//        this.referenceId = referenceId;
//    }
//
//    /**
//     * 级联引用
//     */
//    public Boolean getReferenceTree() {
//        return referenceTree;
//    }
//
//    public void setReferenceTree(Boolean referenceTree) {
//        this.referenceTree = referenceTree;
//    }
}
