package top.lctr.naive.file.system.dto.fileUploadDTO;

import project.extension.openapi.annotations.OpenApiDescription;
import project.extension.openapi.annotations.OpenApiSchema;
import project.extension.openapi.model.OpenApiSchemaType;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;

/**
 * 文件上传业务模型
 * <p>预备上传文件输出信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class PreUploadFileResponse {
    @OpenApiDescription("是否已上传过了（如已上传,则返回个人文件信息）")
    private Boolean uploaded;

    @OpenApiSchema(OpenApiSchemaType.model)
    @OpenApiDescription("个人文件信息（如未上传，此值为空）")
    private PersonalFile personalFile;

    /**
     * 是否已上传过了
     * <p>如已上传,则返回个人文件信息</p>
     */
    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * 个人文件信息
     */
    public PersonalFile getPersonalFile() {
        return personalFile;
    }

    public void setPersonalFile(PersonalFile personalFile) {
        this.personalFile = personalFile;
    }
}
