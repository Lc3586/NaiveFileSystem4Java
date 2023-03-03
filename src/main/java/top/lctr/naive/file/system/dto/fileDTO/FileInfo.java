package top.lctr.naive.file.system.dto.fileDTO;

import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.common.CommonFile;

/**
 * 文件信息业务模型
 * <p>文件信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags({
        @OpenApiMainTag("FileInfo"),
        @OpenApiMainTag(value = "*",
                        level = 1)
})
public class FileInfo
        extends CommonFile {

}
