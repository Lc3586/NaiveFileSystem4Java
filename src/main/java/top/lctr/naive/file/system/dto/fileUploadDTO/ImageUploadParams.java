package top.lctr.naive.file.system.dto.fileUploadDTO;

import org.springframework.web.multipart.MultipartFile;
import project.extension.openapi.annotations.OpenApiDescription;
import project.extension.openapi.annotations.OpenApiSchema;
import project.extension.openapi.model.OpenApiSchemaType;

/**
 * 文件上传业务模型
 * <p>图片上传参数</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class ImageUploadParams {
    @OpenApiSchema(OpenApiSchemaType.file)
    @OpenApiDescription("文件")
    private MultipartFile file;

    @OpenApiDescription("文件名（注意:不指定文件名时将使用原始名称,使用Base64时使用雪花Id）")
    private String name;

    @OpenApiDescription("链接或Base64字符串")
    private String urlOrBase64;

    @OpenApiDescription("下载外链资源")
    private Boolean download = false;

    @OpenApiDescription("图片转Base64链接")
    private Boolean toBase64Url = false;

    @OpenApiDescription("图片转Base64")
    private Boolean toBase64 = false;

    @OpenApiDescription("压缩图片（默认开启）")
    private Boolean isCompress = true;

    @OpenApiDescription("图片压缩选项（默认配置:按照200像素的宽度等比压缩图片，并且保存原图）")
    private ImageCompressOption compressOption = new ImageCompressOption();

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
     * 链接或Base64字符串
     */
    public String getUrlOrBase64() {
        return urlOrBase64;
    }

    public void setUrlOrBase64(String urlOrBase64) {
        this.urlOrBase64 = urlOrBase64;
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
     * 图片转Base64链接
     */
    public Boolean getToBase64Url() {
        return toBase64Url;
    }

    public void setToBase64Url(Boolean toBase64Url) {
        this.toBase64Url = toBase64Url;
    }

    /**
     * 图片转Base64
     */
    public Boolean getToBase64() {
        return toBase64;
    }

    public void setToBase64(Boolean toBase64) {
        this.toBase64 = toBase64;
    }

    /**
     * 压缩图片
     * <p>默认开启</p>
     */
    public Boolean getIsCompress() {
        return isCompress;
    }

    public void setIsCompress(Boolean isCompress) {
        this.isCompress = isCompress;
    }

    /**
     * 图片压缩选项
     * <p>默认配置:按照200像素的宽度等比压缩图片，并且保存原图</p>
     */
    public ImageCompressOption getCompressOption() {
        return compressOption;
    }

    public void setCompressOption(ImageCompressOption compressOption) {
        this.compressOption = compressOption;
    }
}
