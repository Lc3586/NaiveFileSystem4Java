package top.lctr.naive.file.system.dto.fileUploadDTO;

import org.springframework.web.multipart.MultipartFile;
import project.extension.openapi.annotations.OpenApiDescription;

/**
 * 文件上传业务模型
 * <p>分片文件上传参数</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class ChunkFileUploadParams {
    @OpenApiDescription("分片文件")
    private MultipartFile file;

    @OpenApiDescription("上传标识")
    private String key;

    /**
     * 分片文件
     */
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    /**
     * 上传标识
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
