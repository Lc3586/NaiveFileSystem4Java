package top.lctr.naive.file.system.dto.fileUploadConfigDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonFileUploadConfig;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonFileUploadConfig.class)
public class CreateFunUse_Parent {
    /**
     * 文件上传配置Id
     */
    private String id;

    /**
     * 根Id
     */
    private String rootId;

    /**
     * 层级
     */
    private Integer level;

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
     * 根Id
     */
    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    /**
     * 层级
     */
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
