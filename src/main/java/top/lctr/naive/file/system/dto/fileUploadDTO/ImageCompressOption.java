package top.lctr.naive.file.system.dto.fileUploadDTO;

import project.extension.openapi.annotations.OpenApiDescription;

/**
 * 文件上传业务模型
 * <p>图片压缩选项</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class ImageCompressOption {
    /**
     * 如果图片被压缩，是否保存原始图片
     */
    @OpenApiDescription("如果图片被压缩，是否保存原始图片")
    public Boolean saveOriginal = true;

    /**
     * 压缩后的宽度
     * <p>注意:只设置高度时将进行等比压缩</p>
     */
    @OpenApiDescription("压缩后的宽度（注意:只设置高度时将进行等比压缩）")
    public Integer Width = 200;

    /**
     * 压缩后的高度
     */
    @OpenApiDescription("压缩后的高度")
    public Integer height = 0;
}
