package top.lctr.naive.file.system.dto.chunkFileMergeTaskDTO;

import project.extension.openapi.annotations.OpenApiMainTag;
import top.lctr.naive.file.system.entity.common.CommonChunkFileMergeTask;

/**
 * 分片文件合并任务业务模型
 * <p>列表数据</p>
 *
 * @author LCTR
 * @date 2022-04-06
 */
@OpenApiMainTag("List")
public class List
        extends CommonChunkFileMergeTask {
    private String fullName;

    /**
     * 完整名称
     */
    public String getFullName() {
        return String.format("%s%s",
                             this.getName(),
                             this.getExtension());
    }
}
