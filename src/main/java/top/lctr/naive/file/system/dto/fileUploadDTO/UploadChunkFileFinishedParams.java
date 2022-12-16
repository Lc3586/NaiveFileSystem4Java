package top.lctr.naive.file.system.dto.fileUploadDTO;

import project.extension.openapi.annotations.OpenApiDescription;

/**
 * 文件上传业务模型
 * <p>分片文件全部上传完毕参数</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class UploadChunkFileFinishedParams {
    @OpenApiDescription("上传标识")
    private String key;

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
