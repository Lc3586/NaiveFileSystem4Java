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
 * 文件上传配置
 *
 * @author LCTR
 * @date 2022-12-07
 */
@TableSetting
@Alias("CommonFileUploadConfig")
@JSONType(ignores = "serialVersionUID")
@Data
public class CommonFileUploadConfig {
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
                    "Detail",
                    "Edit",
                    "Config",
                    "Authorities"})
    private String id;

    /**
     * 根主键
     */
    @OpenApiDescription("根主键")
    @ColumnSetting(length = 36)
    @OpenApiSubTag({"List",
                    "Detail",
                    "_Import"})
    private String rootId;

    /**
     * 父主键
     */
    @OpenApiDescription("父主键")
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
    @ColumnSetting(isNullable = false)
    @OpenApiSubTag({"List",
                    "Detail",
                    "_Import"})
    private Integer level;

    /**
     * 编码
     */
    @OpenApiDescription("编码")
    @ColumnSetting(length = 36,
                   isNullable = false)
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
    @ColumnSetting(length = 255)
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
    @ColumnSetting(length = 255)
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
    @ColumnSetting(length = -4)
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
    @ColumnSetting(length = -4)
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
    @ColumnSetting(length = -4)
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
    @ColumnSetting(length = -4)
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
    @ColumnSetting(length = 50)
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
    @ColumnSetting(length = 50)
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
}
