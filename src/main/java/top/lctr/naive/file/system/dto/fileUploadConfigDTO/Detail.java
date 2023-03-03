package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import org.springframework.util.StringUtils;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 文件上传配置业务模型
 * <p>详情</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags({
        @OpenApiMainTag("Detail"),
        @OpenApiMainTag(names = {"Detail",
                                 "_Detail"},
                        level = 1)
})
public class Detail
        extends CommonFileUploadConfig {
    private String referenceCode;

    private java.util.List<String> allowedTypeList;

    private java.util.List<String> prohibitedTypeList;

    /**
     * 引用编码
     */
    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    /**
     * 允许的MIME类型
     * <p>此值为空时未禁止即允许</p>
     */
    public java.util.List<String> getAllowedTypeList() {
        return allowedTypeList;
    }

    @Override
    public void setAllowedTypes(String allowedTypes) {
        super.setAllowedTypes(allowedTypes);
        this.allowedTypeList = StringUtils.hasText(allowedTypes)
                               ? Arrays.asList(allowedTypes.split(","))
                               : new ArrayList<>();
    }

    /**
     * 禁止的MIME类型
     * <p>此值为空时皆可允许</p>
     */
    public java.util.List<String> getProhibitedTypeList() {
        return prohibitedTypeList;
    }

    @Override
    public void setProhibitedTypes(String prohibitedTypes) {
        super.setProhibitedTypes(prohibitedTypes);
        this.prohibitedTypeList = StringUtils.hasText(prohibitedTypes)
                                  ? Arrays.asList(prohibitedTypes.split(","))
                                  : new ArrayList<>();
    }
}
