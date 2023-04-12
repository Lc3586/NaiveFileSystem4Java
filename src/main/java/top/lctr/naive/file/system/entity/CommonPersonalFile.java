package top.lctr.naive.file.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import project.extension.mybatis.edge.annotations.ColumnSetting;
import project.extension.mybatis.edge.annotations.TableSetting;
import project.extension.openapi.annotations.*;

import java.util.Date;

/**
 * 个人文件信息
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonPersonalFile")
@JSONType(ignores = "serialVersionUID")
@Data
public class CommonPersonalFile {
    @ColumnSetting(isIgnore = true)
    @OpenApiIgnore
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @OpenApiDescription("主键")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    @ColumnSetting(isPrimaryKey = true,
                   length = 36)
    private String id;

    /**
     * 文件主键
     */
    @OpenApiDescription("文件主键")
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
    @ColumnSetting(length = 255)
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    private String name;

    /**
     * 文件扩展名
     */
    @OpenApiDescription("文件扩展名")
    @OpenApiSubTag({"PersonalFileInfo"})
    @ColumnSetting(length = 50)
    private String extension;

    /**
     * 分类
     */
    @OpenApiDescription("分类")
    @OpenApiSubTag({"PersonalFileInfo",
                    "Edit"})
    @ColumnSetting(length = 50)
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
    @ColumnSetting(length = 50)
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
    @ColumnSetting(length = -4)
    private String remark;

    /**
     * 创建者
     */
    @OpenApiDescription("创建者")
    @ColumnSetting(length = 50)
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
}
