package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import org.springframework.util.StringUtils;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 文件上传配置业务模型
 * <p>配置信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags({
        @OpenApiMainTag(names = {"Config"}),
        @OpenApiMainTag(names = {"Config",
                                 "_Config"},
                        level = 1)
})
public class Config
        extends CommonFileUploadConfig {
//    public Config() {
//
//    }
//
//    public Config(String id,
//                  String displayName,
//                  Integer lowerLimit,
//                  Integer upperLimit,
//                  Float lowerSingleSize,
//                  Float upperSingleSize,
//                  Float lowerTotalSize,
//                  Float upperTotalSize,
//                  String allowedTypes,
//                  String prohibitedTypes,
//                  String explain) {
//        this.setId(id);
//        this.setDisplayName(displayName);
//        this.setLowerLimit(lowerLimit);
//        this.setUpperLimit(upperLimit);
//        this.setAllowedTypes(allowedTypes);
//        this.setProhibitedTypes(prohibitedTypes);
//        this.setExplain(explain);
//    }

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
    }

    @Override
    public void setProhibitedTypes(String prohibitedTypes) {
        super.setProhibitedTypes(prohibitedTypes);
        this.prohibitedTypeList = StringUtils.hasText(prohibitedTypes)
                                  ? Arrays.asList(prohibitedTypes.split(","))
                                  : new ArrayList<>();
    }
}
