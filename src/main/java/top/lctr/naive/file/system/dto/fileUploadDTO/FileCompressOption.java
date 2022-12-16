package top.lctr.naive.file.system.dto.fileUploadDTO;

import project.extension.openapi.annotations.OpenApiDescription;

/**
 * 文件上传业务模型
 * <p>文件压缩选项</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class FileCompressOption {
    @OpenApiDescription("压缩比例（默认0.8）")
    private Double level = 0.8;

    /**
     * 压缩比例
     * <p>默认值：0.8</p>
     */
    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }
}
