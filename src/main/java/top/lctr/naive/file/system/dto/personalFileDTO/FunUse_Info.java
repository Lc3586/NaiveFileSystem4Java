package top.lctr.naive.file.system.dto.personalFileDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import top.lctr.naive.file.system.entity.common.CommonPersonalFile;

/**
 * 文件信息
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping(CommonPersonalFile.class)
public class FunUse_Info {
    /**
     * 个人文件信息Id
     */
    private String id;

    /**
     * 文件Id
     */
    private String fileId;

    /**
     * 文件重命名
     */
    private String name;

    /**
     * 状态
     */
    private String state;

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
     * 文件Id
     */
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 文件重命名
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 状态
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
