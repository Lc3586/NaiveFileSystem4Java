package top.lctr.naive.file.system.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.extension.openapi.annotations.OpenApiAllowAnonymous;
import project.extension.openapi.annotations.OpenApiGroup;
import project.extension.openapi.annotations.OpenApiModel;
import project.extension.openapi.fastjson.JsonExtension;
import project.extension.standard.api.response.ApiResultData;
import top.lctr.naive.file.system.business.service.Interface.IFileUploadService;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadChunkFileResponse;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadFileResponse;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;

/**
 * 文件上传服务控制器
 *
 * @author LCTR
 * @date 2022-04-20
 */
@RestController
@RequestMapping(path = "/common/file-upload",
                consumes = {"application/json",
                            "*/*"},
                produces = "application/json")
@Scope("prototype")
@Api(tags = "文件上传")
@OpenApiGroup("文件服务")
public class FileUploadController
        extends BaseController {
    private final IFileUploadService fileUploadService;

    public FileUploadController(IFileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * 预备上传文件
     *
     * @param configCode 上传配置编码
     * @param md5        文件MD5值
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param length     文件字节数
     * @param filename   文件重命名（可选）
     * @param section    是否分片处理（可选）
     * @param specs      分片文件规格（可选）
     * @param total      分片文件总数（可选）
     * @return 输出信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:pre-file')")
    @GetMapping("/pre-file/{configCode}/{md5}")
    @OpenApiAllowAnonymous
    @ApiOperation("预备上传文件")
    @OpenApiModel(value = PreUploadFileResponse.class,
                  description = "输出信息")
    public Object preUploadFile(
            @Parameter(name = "configCode",
                       description = "上传配置编码")
            @PathVariable("configCode")
                    String configCode,
            @Parameter(name = "md5",
                       description = "文件MD5值")
            @PathVariable("md5")
                    String md5,
            @Parameter(name = "type",
                       description = "文件类型")
            @RequestParam(value = "type")
                    String type,
            @Parameter(name = "extension",
                       description = "文件拓展名")
            @RequestParam(value = "extension")
                    String extension,
            @Parameter(name = "length",
                       description = "文件字节数")
            @RequestParam(value = "length")
                    Long length,
            @Parameter(name = "filename",
                       description = "文件重命名（可选）")
            @RequestParam(value = "filename",
                          required = false)
                    String filename,
            @Parameter(name = "section",
                       description = "是否分片处理（可选）")
            @RequestParam(value = "section",
                          required = false)
                    Boolean section,
            @Parameter(name = "specs",
                       description = "分片文件规格（可选）")
            @RequestParam(value = "specs",
                          required = false)
                    Integer specs,
            @Parameter(name = "total",
                       description = "分片文件总数（可选）")
            @RequestParam(value = "total",
                          required = false)
                    Integer total) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(
                                                   fileUploadService.preUploadFile(configCode,
                                                                                   md5,
                                                                                   type,
                                                                                   extension,
                                                                                   length,
                                                                                   filename,
                                                                                   section,
                                                                                   specs,
                                                                                   total)),
                                           PreUploadFileResponse.class);
    }

    /**
     * 预备上传分片文件
     *
     * @param file_md5 文件MD5值
     * @param md5      分片文件MD5值
     * @param index    分片文件索引
     * @param specs    分片文件规格
     * @param forced   强制上传（可选）
     * @return 输出信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:pre-chunkfile')")
    @GetMapping("/pre-chunkfile/{file_md5}/{md5}/{index}/{specs}")
    @OpenApiAllowAnonymous
    @ApiOperation("预备上传分片文件")
    @OpenApiModel(value = PreUploadChunkFileResponse.class,
                  description = "输出信息")
    public Object preUploadChunkFile(
            @Parameter(name = "file_md5",
                       description = "文件MD5值")
            @PathVariable("file_md5")
                    String file_md5,
            @Parameter(name = "md5",
                       description = "分片文件MD5值")
            @PathVariable("md5")
                    String md5,
            @Parameter(name = "index",
                       description = "分片文件索引")
            @PathVariable("index")
                    Integer index,
            @Parameter(name = "specs",
                       description = "分片文件规格")
            @PathVariable("specs")
                    Integer specs,
            @Parameter(name = "forced",
                       description = "强制上传（可选）")
            @RequestParam(value = "forced",
                          required = false,
                          defaultValue = "false")
                    Boolean forced) {
        return JsonExtension.toOpenApiJson(
                ApiResultData.success(fileUploadService.preUploadChunkFile(file_md5,
                                                                           md5,
                                                                           index,
                                                                           specs,
                                                                           forced)),
                PreUploadChunkFileResponse.class);
    }

    /**
     * 上传单个分片文件
     *
     * @param key  上传标识
     * @param md5  分片文件MD5值
     * @param file 分片文件
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-chunkfile')")
    @PostMapping(value = "/single-chunkfile/{key}/{md5}",
                 consumes = "multipart/form-data")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "上传单个分片文件",
                  consumes = "*/*",
                  response = Object.class)
    public Object singleChunkFile(
            @Parameter(name = "key",
                       description = "上传标识")
            @PathVariable("key")
                    String key,
            @Parameter(name = "md5",
                       description = "分片文件MD5值")
            @PathVariable("md5")
                    String md5,
            @Parameter(name = "file",
                       description = "分片文件")
            @RequestPart("file")
                    MultipartFile file) {
        fileUploadService.singleChunkFile(key,
                                          md5,
                                          file);
        return ApiResultData.success();
    }

    /**
     * 上传单个分片文件
     * <p>通过ArrayBuffer的方式上传</p>
     *
     * @param key 上传标识
     * @param md5 分片文件MD5值
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-chunkfile-arraybuffer')")
    @PostMapping(value = "/single-chunkfile-arraybuffer/{key}/{md5}",
                 consumes = "application/octet-stream")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "上传单个分片文件",
                  notes = "通过ArrayBuffer的方式上传",
                  consumes = "*/*",
                  response = Object.class)
    public Object singleChunkFileByArrayBuffer(
            @Parameter(name = "key",
                       description = "上传标识")
            @PathVariable("key")
                    String key,
            @Parameter(name = "md5",
                       description = "分片文件MD5值")
            @PathVariable("md5")
                    String md5) {
        fileUploadService.singleChunkFileByArrayBuffer(key,
                                                       md5);
        return ApiResultData.success();
    }

    /**
     * 分片文件全部上传完毕
     *
     * @param configCode 文件上传配置
     * @param file_md5   文件MD5值
     * @param specs      分片文件规格
     * @param total      分片文件总数
     * @param type       文件类型（可选）
     * @param extension  文件拓展名（可选）
     * @param filename   文件重命名（可选）
     * @return 个人文件信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:chunkfile-finished')")
    @GetMapping("/chunkfile-finished/{configCode}/{file_md5}/{specs}/{total}")
    @OpenApiAllowAnonymous
    @ApiOperation("分片文件全部上传完毕")
    @OpenApiModel(value = PersonalFile.class,
                  description = "个人文件信息")
    public Object uploadChunkFileFinished(
            @Parameter(name = "configCode",
                       description = "上传配置编码")
            @PathVariable("configCode")
                    String configCode,
            @Parameter(name = "file_md5",
                       description = "文件MD5值")
            @PathVariable("file_md5")
                    String file_md5,
            @Parameter(name = "specs",
                       description = "分片文件规格")
            @PathVariable("specs")
                    Integer specs,
            @Parameter(name = "total",
                       description = "分片文件总数")
            @PathVariable("total")
                    Integer total,
            @Parameter(name = "type",
                       description = "文件类型（可选）")
            @RequestParam(value = "type",
                          required = false)
                    String type,
            @Parameter(name = "extension",
                       description = "文件拓展名（可选）")
            @RequestParam(value = "extension",
                          required = false)
                    String extension,
            @Parameter(name = "filename",
                       description = "文件重命名（可选）")
            @RequestParam(value = "filename",
                          required = false)
                    String filename) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(
                                                   fileUploadService.uploadChunkFileFinished(configCode,
                                                                                             file_md5,
                                                                                             specs,
                                                                                             total,
                                                                                             type,
                                                                                             extension,
                                                                                             filename)),
                                           PersonalFile.class);
    }

    /**
     * 上传单个文件
     * <p>通过Base64字符串上传</p>
     *
     * @param configCode 上传配置编码
     * @param base64     Base64字符串
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param filename   文件重命名（可选）
     * @return 个人文件信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-file-base64')")
    @PostMapping("/single-file-base64/{configCode}")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "上传单个文件",
                  notes = "通过Base64字符串上传")
    @OpenApiModel(value = PersonalFile.class,
                  description = "个人文件信息")
    public Object singleFileFromBase64(
            @Parameter(name = "configCode",
                       description = "上传配置编码")
            @PathVariable("configCode")
                    String configCode,
            @Parameter(name = "base64",
                       description = "Base64字符串")
            @RequestBody
                    String base64,
            @Parameter(name = "type",
                       description = "文件类型")
            @RequestParam("type")
                    String type,
            @Parameter(name = "extension",
                       description = "文件拓展名")
            @RequestParam("extension")
                    String extension,
            @Parameter(name = "filename",
                       description = "文件重命名（可选）")
            @RequestParam("filename")
                    String filename) {
        return JsonExtension.toOpenApiJson(
                ApiResultData.success(
                        fileUploadService.singleFileFromBase64(configCode,
                                                               base64,
                                                               type,
                                                               extension,
                                                               filename)),
                PersonalFile.class);
    }

//    /**
//     * 上传单个文件
//     * <p>通过外链上传</p>
//     *
//     * @param url      外链地址
//     * @param filename 文件重命名（可选）
//     * @param download 是否下载资源（可选）
//     * @return 个人文件信息
//     */
//    @PreAuthorize("@aph.isPass('common:file-upload:single-file-url')")
//    @PostMapping("/single-file-url")
//    @ApiOperation(value = "上传单个文件（权限验证）",
//                  notes = "通过外链上传")
//    @OpenApiModel(value = PersonalFile.class,
//                  description = "个人文件信息")
//    public Object singleFileFromUrl(
//            @Parameter(name = "url",
//                       description = "外链地址")
//            @RequestParam("url")
//                    String url,
//            @Parameter(name = "filename",
//                       description = "文件重命名（可选）")
//            @RequestParam(value = "filename",
//                          required = false)
//                    String filename,
//            @Parameter(name = "download",
//                       description = "是否下载资源（可选）")
//            @RequestParam(value = "download",
//                          required = false,
//                          defaultValue = "false")
//                    Boolean download)
//            throws
//            ServiceException {
//        return JsonExtension.toOpenApiJson(
//                ApiResultData.success(fileUploadService.singleFileFromUrl(url,
//                                                                       filename,
//                                                                       download)),
//                PersonalFile.class);
//    }

//    /**
//     * 上传单个文件
//     * <p>通过外链上传</p>
//     *
//     * @param configCode 上传配置编码
//     * @param url        外链地址
//     * @param filename   文件重命名（可选）
//     * @param download   是否下载资源（可选）
//     * @return 个人文件信息
//     */
////    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-file-url')")
//    @PostMapping("/single-file-url/{configCode}")
//    @OpenApiAllowAnonymous
//    @ApiOperation(value = "上传单个文件",
//                  notes = "通过外链上传")
//    @OpenApiModel(value = PersonalFile.class,
//                  description = "个人文件信息")
//    public Object singleFileFromUrl(
//            @Parameter(name = "configCode",
//                       description = "上传配置编码")
//            @PathVariable("configCode")
//                    String configCode,
//            @Parameter(name = "url",
//                       description = "外链地址")
//            @RequestParam("url")
//                    String url,
//            @Parameter(name = "filename",
//                       description = "文件重命名（可选）")
//            @RequestParam(value = "filename",
//                          required = false)
//                    String filename,
//            @Parameter(name = "download",
//                       description = "是否下载资源（可选）")
//            @RequestParam(value = "download",
//                          required = false,
//                          defaultValue = "false")
//                    Boolean download)
//            throws
//            ServiceException {
//        return JsonExtension.toOpenApiJson(
//                ApiResultData.success(fileUploadService.singleFileFromUrl(configCode,
//                                                                       url,
//                                                                       filename,
//                                                                       download)),
//                PersonalFile.class);
//    }

    /**
     * 上传单个文件
     *
     * @param configCode 上传配置编码
     * @param filename   文件重命名（可选）
     * @param file       文件
     * @return 个人文件信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-file')")
    @PostMapping(value = "/single-file/{configCode}",
                 consumes = "multipart/form-data")
    @OpenApiAllowAnonymous
    @ApiOperation("上传单个文件")
    @OpenApiModel(value = PersonalFile.class,
                  description = "个人文件信息")
    public Object singleFile(
            @Parameter(name = "configCode",
                       description = "上传配置编码")
            @PathVariable("configCode")
                    String configCode,
            @Parameter(name = "filename",
                       description = "文件重命名（可选）")
            @RequestParam(value = "filename",
                          required = false)
                    String filename,
            @Parameter(name = "file",
                       description = "文件")
            @RequestPart("file")
                    MultipartFile file) {
        return JsonExtension.toOpenApiJson(ApiResultData.success(fileUploadService.singleFile(configCode,
                                                                                              filename,
                                                                                              file)),
                                           PersonalFile.class);
    }

    /**
     * 上传单个文件
     * <p>通过ArrayBuffer的方式上传</p>
     *
     * @param configCode 上传配置编码
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param filename   文件重命名（可选）
     * @return 个人文件信息
     */
//    @PreAuthorize("@aph.isPassAllowAnonymous('common:file-upload:single-file-arraybuffer')")
    @PostMapping(value = "/single-file-arraybuffer/{configCode}",
                 consumes = "application/octet-stream")
    @OpenApiAllowAnonymous
    @ApiOperation(value = "上传单个文件",
                  notes = "通过ArrayBuffer的方式上传")
    @OpenApiModel(value = PersonalFile.class,
                  description = "个人文件信息")
    public Object singleFileByArrayBuffer(
            @Parameter(name = "configCode",
                       description = "上传配置编码")
            @PathVariable("configCode")
                    String configCode,
            @Parameter(name = "type",
                       description = "文件类型")
            @RequestParam("type")
                    String type,
            @Parameter(name = "extension",
                       description = "文件拓展名")
            @RequestParam("extension")
                    String extension,
            @Parameter(name = "filename",
                       description = "文件重命名（可选）")
            @RequestParam("filename")
                    String filename) {
        return JsonExtension.toOpenApiJson(
                ApiResultData.success(fileUploadService.singleFileByArrayBuffer(configCode,
                                                                                type,
                                                                                extension,
                                                                                filename)),
                PersonalFile.class);
    }
}
