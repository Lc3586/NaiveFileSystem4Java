package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

/**
 * 文件上传配置业务模型
 * <p>类型信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class Types {
    private String allowedTypes;

    private String prohibitedTypes;

    /**
     * 允许的MIME类型
     */
    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    /**
     * 禁止的MIME类型
     */
    public String getProhibitedTypes() {
        return prohibitedTypes;
    }

    public void setProhibitedTypes(String prohibitedTypes) {
        this.prohibitedTypes = prohibitedTypes;
    }
}
