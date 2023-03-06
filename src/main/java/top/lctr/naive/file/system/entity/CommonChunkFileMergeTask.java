package top.lctr.naive.file.system.entity;

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
import project.extension.openapi.annotations.*;

import java.util.Date;

/**
 * 分片文件合并任务
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonChunkFileMergeTask")
@JSONType(ignores = "serialVersionUID")
public class CommonChunkFileMergeTask {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @OpenApiDescription("主键")
    @OpenApiSubTag({"List",
                    "Detail"})
    @ColumnSetting(isPrimaryKey = true,
                   length = 36)
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
     * 文件MD5校验值
     */
    @OpenApiDescription("文件MD5校验值")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String md5;

    /**
     * 文件名称
     */
    @OpenApiDescription("文件名称")
    @ColumnSetting(length = 255)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String name;

    /**
     * 内容类型
     */
    @OpenApiDescription("内容类型")
    @ColumnSetting(length = 255)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String contentType;

    /**
     * 文件扩展名
     */
    @OpenApiDescription("文件扩展名")
    @ColumnSetting(length = 50)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String extension;

    /**
     * 字节数
     */
    @OpenApiDescription("字节数")
    @OpenApiSubTag({"List",
                    "Detail"})
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long bytes;

    /**
     * 文件大小
     */
    @OpenApiDescription("文件大小")
    @ColumnSetting(length = 255)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String size;

    /**
     * 合并的文件存储路径
     */
    @OpenApiDescription("合并的文件存储路径")
    @ColumnSetting(length = 2048)
    private String path;

    /**
     * 分片规格
     */
    @OpenApiDescription("分片规格")
    @ColumnSetting(isNullable = false)
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer specs;

    /**
     * 分片总数
     */
    @OpenApiDescription("分片总数")
    @ColumnSetting(isNullable = false)
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer total;

    /**
     * 当前处理分片的索引
     */
    @OpenApiDescription("当前处理分片的索引")
    @ColumnSetting(isNullable = false)
    private Integer currentChunkIndex;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.CFMTState
     */
    @OpenApiDescription("状态")
    @ColumnSetting(length = 50)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String state;

    /**
     * 信息
     */
    @OpenApiDescription("信息")
    @ColumnSetting(length = -4)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String info;

    /**
     * 完成时间
     */
    @OpenApiDescription("完成时间")
    @OpenApiSubTag({"List",
                    "Detail"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date completedTime;

    /**
     * 创建时间
     */
    @OpenApiDescription("创建时间")
    @OpenApiSubTag({"List",
                    "Detail"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @OpenApiDescription("更新时间")
    @OpenApiSubTag({"List",
                    "Detail"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public String getId() {
        return id;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    /**
     * 服务器标识
     */
    public String getServerKey() {
        return serverKey;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 文件MD5校验值
     */
    public String getMd5() {
        return md5;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件名称
     */
    public String getName() {
        return name;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 内容类型
     */
    public String getContentType() {
        return contentType;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * 文件扩展名
     */
    public String getExtension() {
        return extension;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    /**
     * 字节数
     */
    public Long getBytes() {
        return bytes;
    }

    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 文件大小
     */
    public String getSize() {
        return size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 合并的文件存储路径
     */
    public String getPath() {
        return path;
    }

    public void setSpecs(Integer specs) {
        this.specs = specs;
    }

    /**
     * 分片规格
     */
    public Integer getSpecs() {
        return specs;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 分片总数
     */
    public Integer getTotal() {
        return total;
    }

    public void setCurrentChunkIndex(Integer currentChunkIndex) {
        this.currentChunkIndex = currentChunkIndex;
    }

    /**
     * 当前处理分片的索引
     */
    public Integer getCurrentChunkIndex() {
        return currentChunkIndex;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.CFMTState
     */
    public String getState() {
        return state;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * 信息
     */
    public String getInfo() {
        return info;
    }

    /**
     * 完成时间
     */
    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
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
                                   ToStringStyle.MULTI_LINE_STYLE).append("id",
                                                                          getId())
                                                                  .append("serverKey",
                                                                          getServerKey())
                                                                  .append("md5",
                                                                          getMd5())
                                                                  .append("name",
                                                                          getName())
                                                                  .append("contentType",
                                                                          getContentType())
                                                                  .append("extension",
                                                                          getExtension())
                                                                  .append("bytes",
                                                                          getBytes())
                                                                  .append("size",
                                                                          getSize())
                                                                  .append("path",
                                                                          getPath())
                                                                  .append("specs",
                                                                          getSpecs())
                                                                  .append("total",
                                                                          getTotal())
                                                                  .append("currentChunkIndex",
                                                                          getCurrentChunkIndex())
                                                                  .append("state",
                                                                          getState())
                                                                  .append("info",
                                                                          getInfo())
                                                                  .append("createTime",
                                                                          getCreateTime())
                                                                  .append("updateTime",
                                                                          getUpdateTime())
                                                                  .append("completedTime",
                                                                          getCompletedTime())
                                                                  .toString();
    }
}
