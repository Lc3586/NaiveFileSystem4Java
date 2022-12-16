package top.lctr.naive.file.system.dto.chunkFileDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import project.extension.mybatis.edge.annotations.EntityMappingSetting;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping
public class FunUse_FileState {
    public FunUse_FileState() {

    }

    public FunUse_FileState(String id,
                            String state,
                            String path) {
        this.id = id;
        this.state = state;
        this.path = path;
    }

    /**
     * Id
     */
    @EntityMappingSetting(self = true)
    private String id;

    /**
     * 状态
     */
    @EntityMappingSetting(self = true)
    private String state;

    /**
     * 路径
     */
    @EntityMappingSetting(self = true)
    private String path;

    /**
     * Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /**
     * 路径
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
