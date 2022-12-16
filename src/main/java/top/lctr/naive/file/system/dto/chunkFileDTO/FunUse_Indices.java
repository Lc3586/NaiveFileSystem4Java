package top.lctr.naive.file.system.dto.chunkFileDTO;

import project.extension.mybatis.edge.annotations.EntityMapping;
import project.extension.mybatis.edge.annotations.EntityMappingSetting;

/**
 *
 * @author LCTR
 * @date 2022-12-07
 */
@EntityMapping
public class FunUse_Indices {
    /**
     * 分片索引
     */
    @EntityMappingSetting(self = true)
    private Integer index;

    /**
     * 分片总数
     */
    @EntityMappingSetting(self = true)
    private Integer count;

    /**
     * 分片索引
     */
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * 路径
     */
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
