package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFileUploadConfig.class)
public class SortFunUse_Target {
    /**
     * 文件上传配置Id
     */
    private String id;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 文件上传配置Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 排序值
     */
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
