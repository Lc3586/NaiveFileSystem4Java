package top.lctr.naive.file.system.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.extension.mybatis.edge.extention.datasearch.DataSearchDTO;
import project.extension.openapi.annotations.*;
import project.extension.openapi.fastjson.ApiDataSchemaExtension;
import project.extension.openapi.fastjson.JsonExtension;
import project.extension.openapi.model.ApiData.*;
import project.extension.standard.api.response.ApiResultData;
import top.lctr.naive.file.system.business.service.Interface.IPersonalFileService;
import top.lctr.naive.file.system.dto.personalFileDTO.Edit;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;

import java.util.Collection;

/**
 * 个人文件信息服务控制器
 *
 * @author LCTR
 * @date 2022-04-20
 */
@RestController
@RequestMapping(path = "/common/personal-file",
                consumes = {"application/json",
                            "*/*"},
                produces = {"application/json",
                            "*/*"})
@Scope("prototype")
@Api(tags = "个人文件信息")
@OpenApiGroup("文件服务")
public class PersonalFileController
        extends BaseController {
    private final IPersonalFileService personalFileService;

    public PersonalFileController(IPersonalFileService personalFileService) {
        this.personalFileService = personalFileService;
    }

    /**
     * 列表数据
     *
     * @param dataSearch 搜索参数
     * @return 列表数据
     */
    @PreAuthorize("@aph.isPass('common:personal-file:list')")
    @PostMapping("/list")
    @ApiOperation("获取列表数据")
    @OpenApiModels(
            defaultDescription = "列表数据",
            defaultGenericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(PersonalFile.class)})},
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
    public Object list(
            @OpenApiModel(description = "搜索参数")
            @RequestBody
                    DataSearchDTO dataSearch) {
        return JsonExtension.toOpenApiJson(
                ApiDataSchemaExtension.buildResult(dataSearch.getSchema(),
                                                   personalFileService.list(dataSearch),
                                                   dataSearch.getPagination()),
                PersonalFile.class);
    }

    /**
     * 详情数据
     *
     * @param id 主键
     * @return 详情数据
     */
//    @PreAuthorize("@aph.isPass('common:personal-file:detail')")
    @GetMapping("/detail/{id}")
    @OpenApiAllowAnonymous
    @ApiOperation("获取详情数据")
    @OpenApiModel(value = PersonalFile.class,
                  description = "详情数据")
    public Object detail(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(personalFileService.detail(id)),
                                           PersonalFile.class);
    }

    /**
     * 详情数据集合
     *
     * @param ids 主键集合
     * @return 配置数据集合
     */
//    @PreAuthorize("@aph.isPass('common:personal-file:detail-list')")
    @PostMapping("/detail-list")
    @OpenApiAllowAnonymous
    @ApiOperation("获取详情数据集合")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(PersonalFile.class)})},
                  description = "详情数据")
    public Object detailList(
            @RequestBody
                    Collection<String> ids) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(personalFileService.detailList(ids)),
                                           PersonalFile.class);
    }

    /**
     * 重命名
     *
     * @param id       数据
     * @param fileName 文件名
     */
    @PreAuthorize("@aph.isPass('common:personal-file:rename')")
    @GetMapping("/rename/{id}/{fileName}")
    @ApiOperation("重命名")
    public Object rename(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id,
            @Parameter(name = "fileName",
                       description = "文件名")
            @PathVariable("fileName")
                    String fileName) {
        personalFileService.rename(id,
                                   fileName);
        return ApiResultData.success();
    }

    /**
     * 获取编辑数据
     *
     * @param id 主键
     * @return 编辑数据
     */
    @PreAuthorize("@aph.isPass('common:personal-file:edit')")
    @GetMapping("/edit/{id}")
    @ApiOperation("获取编辑数据")
    @OpenApiModel(value = Edit.class,
                  description = "编辑数据")
    public Object edit(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(personalFileService.edit(id)),
                                           Edit.class);
    }

    /**
     * 编辑
     *
     * @param data 数据
     */
    @PreAuthorize("@aph.isPass('common:personal-file:edit')")
    @PostMapping("/edit")
    @ApiOperation("编辑")
    public Object edit(
            @OpenApiModel(description = "数据")
            @RequestBody
                    Edit data) {
        personalFileService.edit(data);
        return ApiResultData.success();
    }

    /**
     * 删除
     *
     * @param ids 主键集合
     */
    @PreAuthorize("@aph.isPass('common:personal-file:delete')")
    @PostMapping("/delete")
    @ApiOperation("删除")
    public Object delete(
            @RequestBody
                    Collection<String> ids) {
        personalFileService.delete(ids);
        return ApiResultData.success();
    }

    /**
     * 预览
     *
     * @param id     主键
     * @param width  宽度（可选）
     * @param height 高度（可选）
     * @param time   视频的时间轴位置（示例值：1:59:59）（可选）
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:personal-file:preview')")
    @GetMapping(value = "/preview/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "预览",
                  notes = "不管成功或失败都会尝试输出文件流",
                  consumes = "*/*",
                  response = Object.class)
    public void preview(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id,
            @Parameter(name = "width",
                       description = "宽度（可选）")
            @RequestParam(value = "width",
                          required = false)
                    Integer width,
            @Parameter(name = "height",
                       description = "高度（可选）")
            @RequestParam(value = "height",
                          required = false)
                    Integer height,
            @Parameter(name = "time",
                       description = "视频的时间轴位置（示例值：1:59:59）（可选）")
            @RequestParam(value = "time",
                          required = false)
                    String time) {
        personalFileService.preview(id,
                                    width,
                                    height,
                                    time);
    }

    /**
     * 浏览
     *
     * @param id 主键
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:personal-file:browse')")
    @GetMapping(value = "/browse/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "浏览",
                  notes = "不管成功或失败都会尝试输出文件流",
                  consumes = "*/*",
                  response = Object.class)
    public void browse(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        personalFileService.browse(id);
    }

    /**
     * 下载
     *
     * @param id     主键
     * @param rename 下载文件重命名（可选）
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:personal-file:download')")
    @GetMapping(value = "/download/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "下载",
                  notes = "不管成功或失败都会尝试输出文件流",
                  consumes = "*/*",
                  response = Object.class)
    public void download(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id,
            @Parameter(name = "rename",
                       description = "下载文件重命名（可选）")
            @RequestParam(value = "rename",
                          required = false)
                    String rename) {
        personalFileService.download(id,
                                     rename);
    }

    /**
     * word文件转换为pdf文件
     *
     * @param id 主键
     * @return 详情数据
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:personal-file:word2pdf')")
    @GetMapping(value = "/word2pdf/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("word文件转换为pdf文件")
    @OpenApiModel(value = PersonalFile.class,
                  description = "详情数据")
    public Object word2Pdf(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(personalFileService.word2Pdf(id)),
                                           PersonalFile.class);
    }
}
