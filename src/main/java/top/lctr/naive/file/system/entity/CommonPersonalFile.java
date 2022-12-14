package top.lctr.naive.file.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import project.extension.mybatis.edge.annotations.ColumnSetting;
import project.extension.mybatis.edge.annotations.TableSetting;
import project.extension.openapi.annotations.*;

import java.util.Date;

/**
 * 个人文件信息对象
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@JSONType(ignores = "serialVersionUID")
public class CommonPersonalFile {
    @ColumnSetting(ignore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 个人文件Id
     */
    @OpenApiDescription("个人文件Id")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    @ColumnSetting(primaryKey = true,
                   length = 36)
    private String id;

    /**
     * 文件Id
     */
    @OpenApiDescription("文件Id")
    @OpenApiSubTag({"PersonalFileInfo"})
    @ColumnSetting(length = 36)
    private String fileId;

    /**
     * 文件上传配置编码
     */
    @OpenApiDescription("文件上传配置Id")
    @OpenApiSubTag({"PersonalFileInfo"})
    @ColumnSetting(length = 36)
    private String configCode;

    /**
     * 文件重命名
     */
    @OpenApiDescription("文件重命名")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    private String name;

    /**
     * 文件扩展名
     */
    @OpenApiDescription("文件扩展名")
    @OpenApiSubTag({"PersonalFileInfo"})
    @ColumnSetting(length = 256)
    private String extension;

    /**
     * 分类
     */
    @OpenApiDescription("分类")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    @ColumnSetting(length = 256)
    private String category;

    /**
     * 星级
     */
    @OpenApiDescription("星级")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    private Integer star;

    /**
     * 标签
     */
    @OpenApiDescription("标签")
    @OpenApiSubTag({"_PersonalFileInfo",
                    "_Edit"})
    @ColumnSetting(length = 1000)
    private String tags;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.PersonalFileState
     */
    @OpenApiDescription("状态")
    @OpenApiSubTag({"PersonalFileInfo"})
    @ColumnSetting(length = 10)
    private String state;

    /**
     * 备注
     */
    @OpenApiDescription("备注")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Create",
                    "Edit",
                    "Import",
                    "Export"})
    @ColumnSetting(length = 500)
    private String remark;

    /**
     * 创建者
     */
    @OpenApiDescription("创建者")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Detail",
                    "Import",
                    "Export"})
    private String createBy;

    /**
     * 创建时间
     */
    @OpenApiDescription("创建时间")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Import",
                    "Export"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @OpenApiDescription("更新者")
    @OpenApiSubTag({"PersonalFileInfo",
                    "__Edit",
                    "Import",
                    "Export"})
    private String updateBy;

    /**
     * 更新时间
     */
    @OpenApiDescription("更新时间")
    @OpenApiSubTag({"PersonalFileInfo",
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

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getStar() {
        return star;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
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
                .append("fileId",
                        getFileId())
                .append("configCode",
                        getConfigCode())
                .append("name",
                        getName())
                .append("extension",
                        getExtension())
                .append("category",
                        getCategory())
                .append("star",
                        getStar())
                .append("tags",
                        getTags())
                .append("state",
                        getState())
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
