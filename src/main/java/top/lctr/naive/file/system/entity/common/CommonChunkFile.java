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
import project.extension.openapi.annotations.OpenApiDescription;
import project.extension.openapi.annotations.OpenApiIgnore;
import project.extension.openapi.annotations.OpenApiSubTag;

import java.util.Date;

/**
 * 分片文件信息
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonChunkFile")
@JSONType(ignores = "serialVersionUID")
public class CommonChunkFile {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 分片文件Id
     */
    @OpenApiDescription("分片文件Id")
    @ColumnSetting(isPrimaryKey = true,
                   length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String id;

    /**
     * 服务器标识
     */
    @OpenApiDescription("服务器标识")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String serverKey;

    /**
     * 任务标识
     */
    @OpenApiDescription("任务标识")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String taskKey;

    /**
     * 文件MD5校验值
     */
    @OpenApiDescription("文件MD5校验值")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String fileMd5;

    /**
     * 分片MD5校验值
     */
    @OpenApiDescription("分片MD5校验值")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String md5;

    /**
     * 分片索引
     */
    @OpenApiDescription("分片索引")
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer index;

    /**
     * 分片规格
     */
    @OpenApiDescription("分片规格")
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer specs;

    /**
     * 字节数
     */
    @OpenApiDescription("字节数")
    @OpenApiSubTag({"List",
                    "Detail"})
    private Long bytes;

    /**
     * 文件大小
     */
    @OpenApiDescription("文件大小")
    @OpenApiSubTag({"List",
                    "Detail"})
    private String size;

    /**
     * 文件相对路径
     */
    @OpenApiDescription("文件相对路径")
    @OpenApiSubTag({"List",
                    "Detail"})
    private String path;

    /**
     * 状态（上传中、可用、已删除）
     *
     * @see top.lctr.naive.file.system.dto.FileState
     */
    @OpenApiDescription("状态")
    @OpenApiSubTag({"List",
                    "Detail"})
    private String state;

    /**
     * 创建者
     */
    @OpenApiDescription("创建者")
    @OpenApiSubTag({"Detail"})
    private String createBy;

    /**
     * 创建时间
     */
    @OpenApiDescription("创建时间")
    @OpenApiSubTag({"List",
                    "Detail"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setSpecs(Integer specs) {
        this.specs = specs;
    }

    public Integer getSpecs() {
        return specs;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    public Long getBytes() {
        return bytes;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,
                                   ToStringStyle.MULTI_LINE_STYLE)
                .append("id",
                        getId())
                .append("serverKey",
                        getServerKey())
                .append("taskKey",
                        getTaskKey())
                .append("fileMd5",
                        getFileMd5())
                .append("md5",
                        getMd5())
                .append("index",
                        getIndex())
                .append("specs",
                        getSpecs())
                .append("bytes",
                        getBytes())
                .append("size",
                        getSize())
                .append("path",
                        getPath())
                .append("state",
                        getState())
                .append("createBy",
                        getCreateBy())
                .append("createTime",
                        getCreateTime())
                .toString();
    }
}
