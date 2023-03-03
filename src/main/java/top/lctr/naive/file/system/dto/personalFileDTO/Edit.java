package top.lctr.naive.file.system.dto.personalFileDTO;

import org.springframework.util.StringUtils;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.common.CommonPersonalFile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 个人文件信息业务模型
 * <p>编辑</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags({
        @OpenApiMainTag(names = "Edit"),
        @OpenApiMainTag(names = {"Edit",
                                 "_Edit"},
                        level = 1),
        @OpenApiMainTag(names = {"Edit",
                                 "_Edit",
                                 "__Edit"},
                        level = 2)
})
public class Edit
        extends CommonPersonalFile {
    private java.util.List<String> tagList;

    /**
     * 标签
     */
    public java.util.List<String> getTagList() {
        return tagList;
    }

    public void setTagList(java.util.List<String> tagList) {
        this.tagList = tagList;
        setTags(tagList != null
                ? String.join(",",
                              tagList)
                : "");
    }

    @Override
    public void setTags(String tags) {
        super.setTags(tags);
        this.tagList = StringUtils.hasText(tags)
                       ? Arrays.asList(tags.split(","))
                       : new ArrayList<>();
    }
}
