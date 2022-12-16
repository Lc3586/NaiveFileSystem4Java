package top.lctr.naive.file.system.dto.fileUploadDTO;

import org.springframework.web.multipart.MultipartFile;
import project.extension.openapi.annotations.OpenApiDescription;
import project.extension.openapi.annotations.OpenApiSchema;
import project.extension.openapi.model.OpenApiSchemaType;

/**
 * 文件上传业务模型
 * <p>文件上传参数</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class FileUploadParams {
    @OpenApiSchema(OpenApiSchemaType.file)
    @OpenApiDescription("文件")
    private MultipartFile file;

    @OpenApiDescription("文件名（注意:不指定文件名时将使用原始名称,使用Base64时使用雪花Id）")
    private String name;

    @OpenApiDescription("外链资源链接")
    private String uri;

    @OpenApiDescription("下载外链资源")
    private Boolean download = false;

    @OpenApiDescription("压缩文件（默认关闭）")
    private Boolean isCompress = false;

    @OpenApiDescription("文件压缩选项（默认配置: 压缩比例0.8）")
    private FileCompressOption compressOption = new FileCompressOption();

    /**
     * 文件
     */
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    /**
     * 文件名
     * <p>注意:不指定文件名时将使用原始名称,使用Base64时使用雪花Id</p>
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 外链资源链接
     */
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 下载外链资源
     */
    public Boolean getDownload() {
        return download;
    }

    public void setDownload(Boolean download) {
        this.download = download;
    }

    /**
     * 压缩图片
     * <p>默认关闭</p>
     */
    public Boolean getIsCompress() {
        return isCompress;
    }

    public void setIsCompress(Boolean isCompress) {
        this.isCompress = isCompress;
    }

    /**
     * 文件压缩选项
     * <p>默认配置: 压缩比例0.8</p>
     */
    public FileCompressOption getCompressOption() {
        return compressOption;
    }

    public void setCompressOption(FileCompressOption compressOption) {
        this.compressOption = compressOption;
    }
}
