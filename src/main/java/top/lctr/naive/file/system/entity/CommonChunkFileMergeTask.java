package top.lctr.naive.file.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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
@Data
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
}
