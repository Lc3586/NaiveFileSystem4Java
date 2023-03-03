package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import org.springframework.util.StringUtils;
import project.extension.mybatis.edge.annotations.EntityMapping;
import project.extension.mybatis.edge.annotations.IgnoreEntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFileUploadConfig.class)
public class GetReferenceConfigFunUse_Types {
    /**
     * 允许的MIME类型
     */
    private String allowedTypes;

    @IgnoreEntityMapping
    private java.util.List<String> allowedTypeList;

    /**
     * 禁止的MIME类型
     */
    private String prohibitedTypes;

    @IgnoreEntityMapping
    private java.util.List<String> prohibitedTypeList;

    /**
     * 允许的MIME类型
     */
    public String getAllowedTypes() {
        return allowedTypes;
    }

    /**
     * 允许的MIME类型
     * <p>此值为空时未禁止即允许</p>
     */
    public java.util.List<String> getAllowedTypeList() {
        return allowedTypeList;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
        this.allowedTypeList = StringUtils.hasText(allowedTypes)
                               ? Arrays.asList(allowedTypes.split(","))
                               : new ArrayList<>();
    }

    /**
     * 禁止的MIME类型
     */
    public String getProhibitedTypes() {
        return prohibitedTypes;
    }

    /**
     * 禁止的MIME类型
     * <p>此值为空时皆可允许</p>
     */
    public java.util.List<String> getProhibitedTypeList() {
        return prohibitedTypeList;
    }

    public void setProhibitedTypes(String prohibitedTypes) {
        this.prohibitedTypes = prohibitedTypes;
        this.prohibitedTypeList = StringUtils.hasText(prohibitedTypes)
                                  ? Arrays.asList(prohibitedTypes.split(","))
                                  : new ArrayList<>();
    }
}
