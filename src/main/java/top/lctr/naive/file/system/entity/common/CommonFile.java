package top.lctr.naive.file.system.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.ToStringSerializer;
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
 * 文件信息对象
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonFile")
@JSONType(ignores = "serialVersionUID")
public class CommonFile {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 文件Id
     */
    @OpenApiDescription("文件Id")
    @ColumnSetting(isPrimaryKey = true,
                   length = 36)
    @OpenApiSubTag("FileInfo")
    private String id;

    /**
     * 名称
     */
    @OpenApiDescription("名称")
    @OpenApiSubTag("FileInfo")
    private String name;

    /**
     * 文件类型
     *
     * @see top.lctr.naive.file.system.dto.FileType
     */
    @OpenApiDescription("文件类型")
    @OpenApiSubTag("FileInfo")
    private String fileType;

    /**
     * 内容类型
     */
    @OpenApiDescription("内容类型")
    @OpenApiSubTag("FileInfo")
    private String contentType;

    /**
     * 文件扩展名
     */
    @OpenApiDescription("文件扩展名")
    @OpenApiSubTag("FileInfo")
    private String extension;

    /**
     * MD5校验值
     */
    @OpenApiDescription("MD5校验值")
    @OpenApiSubTag("FileInfo")
    private String md5;

    /**
     * 服务器标识
     */
    @OpenApiDescription("服务器标识")
    @OpenApiSubTag("FileInfo")
    private String serverKey;

    /**
     * 存储类型
     *
     * @see top.lctr.naive.file.system.dto.StorageType
     */
    @OpenApiDescription("存储类型")
    @OpenApiSubTag("FileInfo")
    private String storageType;

    /**
     * 文件相对路径
     */
    @OpenApiDescription("文件相对路径")
    @OpenApiSubTag("_List")
    private String path;

    /**
     * 字节数
     */
    @OpenApiDescription("字节数")
    @OpenApiSubTag("FileInfo")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long bytes;

    /**
     * 文件大小
     */
    @OpenApiDescription("文件大小")
    @OpenApiSubTag("FileInfo")
    private String size;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.FileState
     */
    @OpenApiDescription("状态")
    @OpenApiSubTag("FileInfo")
    private String state;

    /**
     * 创建时间
     */
    @OpenApiDescription("创建时间")
    @OpenApiSubTag("FileInfo")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
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

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
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
                .append("name",
                        getName())
                .append("fileType",
                        getFileType())
                .append("contentType",
                        getContentType())
                .append("extension",
                        getExtension())
                .append("md5",
                        getMd5())
                .append("serverKey",
                        getServerKey())
                .append("storageType",
                        getStorageType())
                .append("path",
                        getPath())
                .append("bytes",
                        getBytes())
                .append("size",
                        getSize())
                .append("state",
                        getState())
                .append("createTime",
                        getCreateTime())
                .toString();
    }
}
