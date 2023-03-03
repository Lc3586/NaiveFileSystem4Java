package top.lctr.naive.file.system.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;
import project.extension.mybatis.edge.annotations.ColumnSetting;
import project.extension.mybatis.edge.annotations.TableSetting;
import project.extension.openapi.annotations.*;

import java.util.Date;

/**
 * 文件上传配置对象
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonFileUploadConfig")
@JSONType(ignores = "serialVersionUID")
public class CommonFileUploadConfig {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 文件上传配置Id
     */
    @OpenApiDescription("文件上传配置Id")
    @ColumnSetting(isPrimaryKey = true,
                   length = 36)
    @OpenApiSubTag({"List",
                    "Detail",
                    "Edit",
                    "Config",
                    "Authorities"})
    private String id;

    /**
     * 根Id
     */
    @OpenApiDescription("根Id")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail",
                    "_Import"})
    private String rootId;

    /**
     * 父Id
     */
    @OpenApiDescription("父Id")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Create",
                    "Detail",
                    "_Import",
                    "_Export"})
    private String parentId;

    /**
     * 层级
     */
    @OpenApiDescription("层级")
    @OpenApiSubTag({"List",
                    "Detail",
                    "_Import"})
    private Integer level;

    /**
     * 编码
     */
    @OpenApiDescription("编码")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config",
                    "Authorities"})
    private String code;

    /**
     * 引用的上传配置Id
     * <p>1、引用文件MIME类型，会合并当前数据以及引用数据</p>
     */
    @OpenApiDescription("引用的上传配置Id")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "_Import",
                    "_Export"})
    private String referenceId;

    /**
     * 级联引用
     * <p>1、使用引用的上传配置以及它的所有子集配置</p>
     */
    @OpenApiDescription("级联引用")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export"})
    private Boolean referenceTree;

    /**
     * 名称
     */
    @OpenApiDescription("名称")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Authorities"})
    private String name;

    /**
     * 显示名称
     */
    @OpenApiDescription("显示名称")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config",
                    "Authorities"})
    private String displayName;

    /**
     * 公共配置（无需授权）
     */
    @OpenApiDescription("公共配置")
    @ColumnSetting(alias = "public")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Authorities"})
    private Boolean public_;

    /**
     * 文件数量下限
     */
    @OpenApiDescription("文件数量下限")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Integer lowerLimit;

    /**
     * 文件数量上限
     */
    @OpenApiDescription("文件数量上限")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Integer upperLimit;

    /**
     * 单个文件大小下限（单位 KB）
     */
    @OpenApiDescription("单个文件大小下限（单位 KB）")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Double lowerSingleSize;

    /**
     * 单个文件大小上限（单位 KB）
     */
    @OpenApiDescription("单个文件大小上限（单位 KB）")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Double upperSingleSize;

    /**
     * 所有文件整体大小下限（单位 KB）
     */
    @OpenApiDescription("所有文件整体大小下限（单位 KB）")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Double lowerTotalSize;

    /**
     * 所有文件整体大小上限（单位 KB）
     */
    @OpenApiDescription("所有文件整体大小上限（单位 KB）")
    @OpenApiSubTag({"List",
                    "Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private Double upperTotalSize;

    /**
     * 允许的MIME类型
     * <p>1、[,]逗号分隔</p>
     * <p>2、此值为空时未禁止即允许</p>
     */
    @OpenApiDescription("允许的MIME类型")
    @OpenApiSubTag({"_Edit",
                    "_Detail",
                    "_Config",
                    "Import",
                    "Export"})
    private String allowedTypes;

    /**
     * 禁止的MIME类型
     * <p>1、[,]逗号分隔</p>
     * <p>2、此值为空时皆可允许</p>
     */
    @OpenApiDescription("禁止的MIME类型")
    @OpenApiSubTag({"_Edit",
                    "_Detail",
                    "_Config",
                    "Import",
                    "Export"})
    private String prohibitedTypes;

    /**
     * 说明
     */
    @OpenApiDescription("说明")
    @ColumnSetting(length = -1)
    @OpenApiSubTag({"Create",
                    "Edit",
                    "Detail",
                    "Import",
                    "Export",
                    "Config"})
    private String explain;

    /**
     * 排序值
     */
    @OpenApiDescription("排序值")
    @OpenApiSubTag({"List",
                    "Sort",
                    "_Import"})
    private Integer sort;

    /**
     * 启用
     */
    @OpenApiDescription("启用")
    @OpenApiSubTag({"List",
                    "Create",
                    "Detail",
                    "Import",
                    "Export",
                    "_Config"})
    private Boolean enable;

    /**
     * 备注
     */
    @OpenApiDescription("备注")
    @OpenApiSubTag({"Detail",
                    "Create",
                    "Edit",
                    "Import",
                    "Export"})
    private String remark;

    /**
     * 创建者
     */
    @OpenApiDescription("创建者")
    @OpenApiSubTag({"List",
                    "Detail",
                    "Import",
                    "Export"})
    private String createBy;

    /**
     * 创建时间
     */
    @OpenApiDescription("创建时间")
    @OpenApiSubTag({"List",
                    "Detail",
                    "Import",
                    "Export"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @OpenApiDescription("更新者")
    @OpenApiSubTag({"Detail",
                    "__Edit",
                    "Import",
                    "Export"})
    private String updateBy;

    /**
     * 更新时间
     */
    @OpenApiDescription("更新时间")
    @OpenApiSubTag({"List",
                    "Detail",
                    "__Edit",
                    "Import",
                    "Export"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceTree(Boolean referenceTree) {
        this.referenceTree = referenceTree;
    }

    public Boolean getReferenceTree() {
        return referenceTree;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPublic_(Boolean public_) {
        this.public_ = public_;
    }

    public Boolean getPublic_() {
        return public_;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public Double getLowerSingleSize() {
        return lowerSingleSize;
    }

    public void setLowerSingleSize(Double lowerSingleSize) {
        this.lowerSingleSize = lowerSingleSize;
    }

    public Double getUpperSingleSize() {
        return upperSingleSize;
    }

    public void setUpperSingleSize(Double upperSingleSize) {
        this.upperSingleSize = upperSingleSize;
    }

    public Double getLowerTotalSize() {
        return lowerTotalSize;
    }

    public void setLowerTotalSize(Double lowerTotalSize) {
        this.lowerTotalSize = lowerTotalSize;
    }

    public Double getUpperTotalSize() {
        return upperTotalSize;
    }

    public void setUpperTotalSize(Double upperTotalSize) {
        this.upperTotalSize = upperTotalSize;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setProhibitedTypes(String prohibitedTypes) {
        this.prohibitedTypes = prohibitedTypes;
    }

    public String getProhibitedTypes() {
        return prohibitedTypes;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getExplain() {
        return explain;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return sort;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    /**
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 创建者
     */
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新者
     */
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,
                                   ToStringStyle.MULTI_LINE_STYLE)
                .append("id",
                        getId())
                .append("rootId",
                        getRootId())
                .append("parentId",
                        getParentId())
                .append("level",
                        getLevel())
                .append("code",
                        getCode())
                .append("referenceId",
                        getReferenceId())
                .append("referenceTree",
                        getReferenceTree())
                .append("name",
                        getName())
                .append("displayName",
                        getDisplayName())
                .append("public",
                        getPublic_())
                .append("lowerLimit",
                        getLowerLimit())
                .append("upperLimit",
                        getUpperLimit())
                .append("lowerSingleSize",
                        getLowerSingleSize())
                .append("upperSingleSize",
                        getUpperSingleSize())
                .append("lowerTotalSize",
                        getLowerTotalSize())
                .append("upperTotalSize",
                        getUpperTotalSize())
                .append("allowedTypes",
                        getAllowedTypes())
                .append("prohibitedTypes",
                        getProhibitedTypes())
                .append("explain",
                        getExplain())
                .append("sort",
                        getSort())
                .append("enable",
                        getEnable())
                .append("remark",
                        getRemark())
                .append("createBy",
                        getCreateBy())
                .append("createTime",
                        getCreateTime())
                .append("updateBy",
                        getUpdateBy())
                .append("updateTime",
                        getUpdateTime())
                .toString();
    }
}
