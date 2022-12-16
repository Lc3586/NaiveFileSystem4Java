package top.lctr.naive.file.system.dto.fileUploadDTO;

import project.extension.openapi.annotations.OpenApiDescription;

/**
 * 文件上传业务模型
 * <p>预备上传分片文件输出信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class PreUploadChunkFileResponse {
    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.PersonalFileState
     */
    @OpenApiDescription("状态")
    private String state;

    /**
     * 上传标识
     */
    @OpenApiDescription("上传标识")
    private String key;

    /**
     * 状态
     *
     * @see top.lctr.naive.file.system.dto.PersonalFileState
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
