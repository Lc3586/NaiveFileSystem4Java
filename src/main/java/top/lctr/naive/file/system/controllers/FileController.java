package top.lctr.naive.file.system.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.extension.file.VideoInfo;
import project.extension.mybatis.edge.extention.datasearch.DataSearchDTO;
import project.extension.openapi.annotations.*;
import project.extension.openapi.fastjson.ApiDataSchemaExtension;
import project.extension.openapi.fastjson.JsonExtension;
import project.extension.openapi.model.ApiData.*;
import project.extension.standard.api.response.ApiResultData;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.dto.fileDTO.FileInfo;
import top.lctr.naive.file.system.dto.fileDTO.LibraryInfo;

import java.util.Collection;

/**
 * 文件信息服务控制器
 *
 * @author LCTR
 * @date 2022-04-20
 */
@RestController
@RequestMapping(path = "/common/file",
                consumes = "application/json",
                produces = "application/json")
@Scope("prototype")
@Api(tags = "文件信息")
@OpenApiGroup("文件服务")
public class FileController
        extends BaseController {
    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 列表数据
     *
     * @param dataSearch 搜索参数
     * @return 列表数据
     */
    @PreAuthorize("@aph.isPass('common:file:list')")
    @PostMapping("/list")
    @ApiOperation("获取列表数据")
    @OpenApiModels(defaultGenericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(FileInfo.class)})},
                   defaultDescription = "列表数据",
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
        return JsonExtension.toOpenApiJson(ApiDataSchemaExtension.buildResult(dataSearch.getSchema(),
                                                                              fileService.list(dataSearch),
                                                                              dataSearch.getPagination()),
                                           FileInfo.class);
    }

    /**
     * 详情数据
     *
     * @param id 主键
     * @return 详情数据
     */
    @PreAuthorize("@aph.isPass('common:file:detail')")
    @GetMapping(value = "/detail/{id}",
                consumes = "*/*")
    @ApiOperation("获取详情数据")
    @OpenApiModel(value = FileInfo.class,
                  description = "详情数据")
    public Object detail(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileService.detail(id)),
                                           FileInfo.class);
    }

    /**
     * 详情数据集合
     *
     * @param ids 主键集合
     * @return 详情数据集合
     */
    @PreAuthorize("@aph.isPass('common:file:detail-list')")
    @PostMapping("/detail-list")
    @ApiOperation("获取详情数据集合")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(FileInfo.class)})},
                  description = "详情数据")
    public Object detailList(
            @RequestBody
                    Collection<String> ids) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileService.detailList(ids)),
                                           FileInfo.class);
    }

    /**
     * 删除
     *
     * @param ids 主键集合
     */
    @PreAuthorize("@aph.isPass('common:file:delete')")
    @PostMapping("/delete")
    @ApiOperation("删除")
    public Object delete(
            @RequestBody
                    Collection<String> ids) {
        fileService.delete(ids);
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
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:preview')")
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
                       description = "视频的时间轴位置（默认值：00:00:00.0010000）（可选）")
            @RequestParam(value = "time",
                          required = false)
                    String time) {
        fileService.preview(id,
                            width,
                            height,
                            time);
    }

    /**
     * 浏览
     *
     * @param id 主键
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:browse')")
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
        fileService.browse(id);
    }

    /**
     * 下载
     *
     * @param id     主键
     * @param rename 下载文件重命名（可选）
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:download')")
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
        fileService.download(id,
                             rename);
    }

    /**
     * 获取文件类型
     *
     * @param extension 文件拓展名
     * @return 文件类型
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:type-by-extension')")
    @GetMapping(value = "/type-by-extension/{extension}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取文件类型")
    @OpenApiModel(value = String.class,
                  description = "文件类型")
    public Object fileTypeByExtension(
            @Parameter(name = "extension",
                       description = "文件拓展名")
            @PathVariable("extension")
                    String extension) {
        return ApiResultData.success(fileService.fileTypeByExtension(extension));
    }

    /**
     * 获取文件类型
     *
     * @param mimetype 文件内容类型
     * @return 文件类型
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:type-by-mimetype')")
    @GetMapping(value = "/type-by-mimetype/{mimetype}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取文件类型")
    @OpenApiModel(value = String.class,
                  description = "文件类型")
    public Object fileTypeByMIME(
            @Parameter(name = "extension",
                       description = "文件内容类型")
            @PathVariable("mimetype")
                    String mimetype) {
        return ApiResultData.success(fileService.fileTypeByMIME(mimetype));
    }

    /**
     * 获取文件类型预览图
     *
     * @param extension 文件拓展名
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:type-image')")
    @GetMapping(value = "/type-image/{extension}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "获取文件类型预览图",
                  notes = "不管成功或失败都会尝试输出文件流",
                  consumes = "*/*",
                  response = Object.class)
    public void fileTypeImage(
            @Parameter(name = "extension",
                       description = "文件拓展名")
            @PathVariable("extension")
                    String extension) {
        fileService.fileTypeImage(extension);
    }

    /**
     * 获取文件大小描述信息
     *
     * @param length 文件字节数
     * @return 文件大小描述信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:size')")
    @GetMapping(value = "/size/{length}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取文件大小")
    @OpenApiModel(value = String.class,
                  description = "文件大小描述信息")
    public Object fileSize(
            @Parameter(name = "length",
                       description = "文件字节数")
            @PathVariable("length")
                    String length) {
        return ApiResultData.success(fileService.fileSize(length));
    }

    /**
     * 获取视频信息
     *
     * @param id       主键
     * @param format   获取有关输入多媒体流的容器格式的信息（可选）
     * @param streams  获取有关输入多媒体流中包含的每个媒体流的信息（可选）
     * @param chapters 获取有关以该格式存储的章节的信息（可选）
     * @param programs 获取有关程序及其输入多媒体流中包含的流的信息（可选）
     * @param version  获取与程序版本有关的信息、获取与库版本有关的信息、获取与程序和库版本有关的信息（可选）
     * @return 视频信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:video-info')")
    @GetMapping(value = "/video-info/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取视频信息")
    @OpenApiModel(value = VideoInfo.class,
                  description = "视频信息")
    public Object videoInfo(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id,
            @Parameter(name = "format",
                       description = "获取有关输入多媒体流的容器格式的信息（可选）")
            @RequestParam(value = "format",
                          required = false,
                          defaultValue = "true")
                    Boolean format,
            @Parameter(name = "streams",
                       description = "获取有关输入多媒体流中包含的每个媒体流的信息（可选）")
            @RequestParam(value = "streams",
                          required = false,
                          defaultValue = "true")
                    Boolean streams,
            @Parameter(name = "chapters",
                       description = "获取有关以该格式存储的章节的信息（可选）")
            @RequestParam(value = "chapters",
                          required = false,
                          defaultValue = "true")
                    Boolean chapters,
            @Parameter(name = "programs",
                       description = "获取有关程序及其输入多媒体流中包含的流的信息（可选）")
            @RequestParam(value = "programs",
                          required = false,
                          defaultValue = "true")
                    Boolean programs,
            @Parameter(name = "version",
                       description = "获取与程序版本有关的信息、获取与库版本有关的信息、获取与程序和库版本有关的信息（可选）")
            @RequestParam(value = "version",
                          required = false,
                          defaultValue = "true")
                    Boolean version) {
        return ApiResultData.success(fileService.videoInfo(id,
                                                           format,
                                                           streams,
                                                           chapters,
                                                           programs,
                                                           version));
    }

    /**
     * 获取文件库信息
     *
     * @return 文件库信息
     */
    @PreAuthorize("@aph.isPass('common:file:library-info')")
    @GetMapping(value = "/library-info",
                consumes = "*/*")
    @ApiOperation("获取文件库信息")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(LibraryInfo.class)})},
                  description = "文件库信息")
    public Object libraryInfo() {
        return ApiResultData.success(fileService.libraryInfo());
    }

    /**
     * 获取所有文件类型
     *
     * @return 文件类型集合
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:filetypes')")
    @GetMapping(value = "/filetypes",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取所有文件类型")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(String.class)})},
                  description = "文件类型")
    public Object fileTypes() {
        return ApiResultData.success(fileService.fileTypes());
    }

    /**
     * 获取所有文件存储类型
     *
     * @return 文件存储类型集合
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:storagetypes')")
    @GetMapping(value = "/storagetypes",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取所有文件存储类型")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(String.class)})},
                  description = "文件存储类型")
    public Object storageTypes() {
        return ApiResultData.success(fileService.storageTypes());
    }

    /**
     * 获取所有文件状态
     *
     * @return 文件状态集合
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:filestates')")
    @GetMapping(value = "/filestates",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("获取所有文件状态")
    @OpenApiModel(type = java.util.List.class,
                  genericTypes = {@OpenApiGenericType(arguments = {@OpenApiGenericTypeArgument(String.class)})},
                  description = "文件状态")
    public Object fileStates() {
        return ApiResultData.success(fileService.fileStates());
    }

    /**
     * word文件转换为pdf文件
     *
     * @param id 主键
     * @return 详情数据
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file:word2pdf')")
    @GetMapping(value = "/word2pdf/{id}",
                consumes = "*/*")
    @OpenApiAllowAnonymous
    @ApiOperation("word文件转换为pdf文件")
    @OpenApiModel(value = FileInfo.class,
                  description = "详情数据")
    public Object word2Pdf(
            @Parameter(name = "id",
                       description = "主键")
            @PathVariable("id")
                    String id) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileService.word2PdfAndReturnFileInfo(id)),
                                           FileInfo.class);
    }

//    @GetMapping(value = "/poi-version",
//                consumes = "*/*")
//    @OpenApiAllowAnonymous
//    public void getPoiVersion(HttpServletResponse response)
//            throws
//            IOException {
//        response.setHeader("content-type",
//                           "text/plan;charset=UTF-8");
//        response.setStatus(200);
//        PrintWriter writer = response.getWriter();
//        writer.println(String.format("%sDocumentDocument",
//                                     DocumentDocument.class.getResource("")
//                                                           .getPath()));
//        writer.println(String.format("%sXWPFDocument",
//                                     XWPFDocument.class.getResource("")
//                                                       .getPath()));
//        writer.println(String.format("%sIFontProvider",
//                                     IFontProvider.class.getResource("")
//                                                        .getPath()));
//    }
}
