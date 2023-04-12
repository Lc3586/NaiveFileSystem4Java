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
 * 文件信息
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonFile")
@JSONType(ignores = "serialVersionUID")
@Data
public class CommonFile {
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
    @OpenApiSubTag("FileInfo")
    private String id;

    /**
     * 名称
     */
    @OpenApiDescription("名称")
    @ColumnSetting(length = 255)
    @OpenApiSubTag("FileInfo")
    private String name;

    /**
     * 文件类型
     *
     * @see top.lctr.naive.file.system.dto.FileType
     */
    @OpenApiDescription("文件类型")
    @ColumnSetting(length = 50)
    @OpenApiSubTag("FileInfo")
    private String fileType;

    /**
     * 内容类型
     */
    @OpenApiDescription("内容类型")
    @ColumnSetting(length = 255)
    @OpenApiSubTag("FileInfo")
    private String contentType;

    /**
     * 文件扩展名
     */
    @OpenApiDescription("文件扩展名")
    @ColumnSetting(length = 50)
    @OpenApiSubTag("FileInfo")
    private String extension;

    /**
     * 文件MD5校验值
     */
    @OpenApiDescription("文件MD5校验值")
    @ColumnSetting(length = 36)
    @OpenApiSubTag("FileInfo")
    private String md5;

    /**
     * 服务器标识
     */
    @OpenApiDescription("服务器标识")
    @ColumnSetting(length = 36)
    @OpenApiSubTag("FileInfo")
    private String serverKey;

    /**
     * 存储类型
     *
     * @see top.lctr.naive.file.system.dto.StorageType
     */
    @OpenApiDescription("存储类型")
    @ColumnSetting(length = 50)
    @OpenApiSubTag("FileInfo")
    private String storageType;

    /**
     * 文件相对路径
     */
    @OpenApiDescription("文件相对路径")
    @ColumnSetting(length = 2048)
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
    @ColumnSetting(length = 255)
    @OpenApiSubTag("FileInfo")
    private String size;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.FileState
     */
    @OpenApiDescription("状态")
    @ColumnSetting(length = 50)
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
}
