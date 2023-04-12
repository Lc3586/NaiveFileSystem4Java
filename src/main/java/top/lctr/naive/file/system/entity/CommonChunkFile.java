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
@Data
public class CommonChunkFile {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @OpenApiDescription("主键")
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
    @ColumnSetting(isNullable = false)
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer index;

    /**
     * 分片规格
     */
    @OpenApiDescription("分片规格")
    @ColumnSetting(isNullable = false)
    @OpenApiSubTag({"List",
                    "Detail"})
    private Integer specs;

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
     * 文件相对路径
     */
    @OpenApiDescription("文件相对路径")
    @ColumnSetting(length = 2048)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String path;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.FileState
     */
    @OpenApiDescription("状态")
    @ColumnSetting(length = 50)
    @OpenApiSubTag({"List",
                    "Detail"})
    private String state;

    /**
     * 创建者
     */
    @OpenApiDescription("创建者")
    @ColumnSetting(length = 50)
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
}
