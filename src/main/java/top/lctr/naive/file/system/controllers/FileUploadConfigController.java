package top.lctr.naive.file.system.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.extension.mybatis.edge.extention.datasearch.TreeDataSearchDTO;
import project.extension.openapi.annotations.*;
import project.extension.openapi.fastjson.ApiDataSchemaExtension;
import project.extension.openapi.fastjson.JsonExtension;
import project.extension.openapi.model.ApiData.*;
import project.extension.standard.api.request.datasort.DataSortDTO;
import project.extension.standard.api.request.datasort.TreeDragSortDTO;
import project.extension.standard.api.response.ApiResultData;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.business.service.Interface.IFileUploadConfigService;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.Config;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.Create;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.Detail;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.TreeList;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.Edit;

import java.util.Collection;

/**
 * 文件上传配置服务控制器
 *
 * @author LCTR
 * @date 2022-04-11
 */
@RestController
@RequestMapping(path = "/common/file-upload-config",
                consumes = "application/json",
                produces = "application/json")
@Scope("prototype")
@Api(tags = "文件上传配置")
@OpenApiGroup("文件服务")
public class FileUploadConfigController
        extends BaseController {
    private final IFileUploadConfigService fileUploadConfigService;

    public FileUploadConfigController(IFileUploadConfigService fileUploadConfigService) {
        this.fileUploadConfigService = fileUploadConfigService;
    }

    /**
     * 树状列表数据
     *
     * @param dataSearch 搜索参数
     * @return 数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:tree-list')")
    @PostMapping("/tree-list")
    @ApiOperation("获取树状列表数据")
    @OpenApiModels(defaultGenericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(TreeList.class)})},
                   defaultDescription = "树状列表数据",
                   value = {@OpenApiModel(type = RuoyiResult.class,
                                          summary = "Ruoyi框架数据结构方案（默认）",
                                          defaultModel = true),
                            @OpenApiModel(type = LayuiResult.class,
                                          summary = "Layui数据结构方案"),
                            @OpenApiModel(type = JqGridResult.class,
                                          summary = "JqGrid数据结构方案"),
                            @OpenApiModel(type = EasyuiResult.class,
                                          summary = "Easyui、BootstrapTable数据结构方案"),
                            @OpenApiModel(type = AntdVueResult.class,
                                          summary = "AntdVue数据结构方案"),
                            @OpenApiModel(type = ElementVueResult.class,
                                          summary = "ElementVue数据结构方案")})
    public Object treeList(
            @OpenApiModel(description = "搜索参数")
            @RequestBody
                    TreeDataSearchDTO dataSearch) {
        return JsonExtension.toOpenApiJson(ApiDataSchemaExtension.buildResult(dataSearch.getSchema(),
                                                                              fileUploadConfigService.treeList(dataSearch),
                                                                              dataSearch.getPagination()),
                                           TreeList.class);
    }

    /**
     * 详情数据
     *
     * @param id 主键
     * @return 详情数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:detail')")
    @GetMapping(value = "/detail/{id}",
                consumes = "*/*")
    @ApiOperation("获取详情数据")
    @OpenApiModel(value = Detail.class,
                  description = "详情数据")
    public Object detail(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileUploadConfigService.detail(id)),
                                           Detail.class);
    }

    /**
     * 配置数据
     *
     * @param code 编码
     * @return 配置数据
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload-config:config')")
    @GetMapping(value = "/config/{code}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取配置数据")
    @OpenApiModel(value = Config.class,
                  description = "配置数据")
    public Object config(
            @Parameter(name = "code",
                       description = "编码")
            @PathVariable("code")
                    String code) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileUploadConfigService.config(code,
                                                                                                false)),
                                           Config.class);
    }

    /**
     * 配置数据集合
     *
     * @param codes 编码集合
     * @return 配置数据集合
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload-config:config-list')")
    @PostMapping("/config-list")
    @OpenApiAllowAnonymous
    @ApiOperation("获取配置数据集合")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(Config.class)})},
                  description = "配置数据")
    public Object configList(
            @RequestBody
                    Collection<String> codes) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileUploadConfigService.configList(codes)),
                                           Config.class);
    }

    /**
     * 新增
     *
     * @param data 数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:create')")
    @PostMapping("/create")
    @ApiOperation("新增")
    public Object create(
            @OpenApiModel(description = "数据")
            @RequestBody
                    Create data) {
        fileUploadConfigService.create(data);
        return ApiResultData.success();
    }

    /**
     * 获取编辑数据
     *
     * @param id 主键
     * @return 编辑数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:edit')")
    @GetMapping(value = "/edit/{id}",
                consumes = "*/*")
    @ApiOperation("获取编辑数据")
    @OpenApiModel(value = Edit.class,
                  description = "编辑数据")
    public Object edit(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileUploadConfigService.edit(id)),
                                           Edit.class);
    }

    /**
     * 编辑
     *
     * @param data 数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:edit')")
    @PostMapping("/edit")
    @ApiOperation("编辑")
    public Object edit(
            @OpenApiModel(description = "数据")
            @RequestBody
                    Edit data)
            throws
            BusinessException {
        fileUploadConfigService.edit(data);
        return ApiResultData.success();
    }

    /**
     * 删除
     *
     * @param ids 主键集合
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:delete')")
    @PostMapping("/delete")
    @ApiOperation("删除")
    public Object delete(
            @RequestBody
                    Collection<String> ids) {
        fileUploadConfigService.delete(ids);
        return ApiResultData.success();
    }

    /**
     * 排序
     *
     * @param data 数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:sort')")
    @PostMapping("/sort")
    @ApiOperation("排序")
    public Object sort(
            @OpenApiModel(description = "数据")
            @RequestBody
                    DataSortDTO<String> data) {
        fileUploadConfigService.sort(data);
        return ApiResultData.success();
    }

    /**
     * 拖动排序
     *
     * @param data 数据
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:drag-sort')")
    @PostMapping("/drag-sort")
    @ApiOperation("拖动排序")
    public Object dragSort(
            @OpenApiModel(description = "数据")
            @RequestBody
                    TreeDragSortDTO<String> data) {
        fileUploadConfigService.dragSort(data);
        return ApiResultData.success();
    }

    /**
     * 启用/禁用
     *
     * @param id     主键
     * @param enable true：启用，false：禁用
     */
    @PreAuthorize("@aph.isPass('common:file-upload-config:enable')")
    @GetMapping(value = "/enable/{id}/{enable}",
                consumes = "*/*")
    @ApiOperation("启用/禁用")
    public Object enable(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id,
            @Parameter(name = "enable",
                       description = "true：启用，false：禁用")
            @PathVariable("enable")
                    Boolean enable) {
        fileUploadConfigService.enable(id,
                                       enable);
        return ApiResultData.success();
    }
}
