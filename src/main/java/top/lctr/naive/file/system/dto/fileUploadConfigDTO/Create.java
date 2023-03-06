package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import org.springframework.util.StringUtils;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.CommonFileUploadConfig;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 文件上传配置业务模型
 * <p>新增</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags({
        @OpenApiMainTag("Create"),
        @OpenApiMainTag(names = "*",
                        level = 1)
})
public class Create
        extends CommonFileUploadConfig {
    private java.util.List<String> allowedTypeList;

    private java.util.List<String> prohibitedTypeList;

    /**
     * 允许的MIME类型
     * <p>此值为空时未禁止即允许</p>
     */
    public java.util.List<String> getAllowedTypeList() {
        return allowedTypeList;
    }

    public void setAllowedTypeList(java.util.List<String> allowedTypeList) {
        this.allowedTypeList = allowedTypeList;
        setAllowedTypes(allowedTypeList != null
                        ? String.join(",",
                                      allowedTypeList)
                        : "");
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

    public void setProhibitedTypeList(java.util.List<String> prohibitedTypeList) {
        this.prohibitedTypeList = prohibitedTypeList;
        setProhibitedTypes(prohibitedTypeList != null
                           ? String.join(",",
                                         prohibitedTypeList)
                           : "");
    }

    @Override
    public void setProhibitedTypes(String prohibitedTypes) {
        super.setProhibitedTypes(prohibitedTypes);
        this.prohibitedTypeList = StringUtils.hasText(prohibitedTypes)
                                  ? Arrays.asList(prohibitedTypes.split(","))
                                  : new ArrayList<>();
    }
}
