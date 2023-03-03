package top.lctr.naive.file.system.dto.personalFileDTO;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;
import project.extension.mybatis.edge.annotations.EntityMapping;
import project.extension.mybatis.edge.annotations.EntityMappingSetting;
import project.extension.openapi.annotations.OpenApiDescription;
import project.extension.openapi.annotations.OpenApiMainTag;
import project.extension.openapi.annotations.OpenApiMainTags;
import top.lctr.naive.file.system.entity.common.CommonFile;
import top.lctr.naive.file.system.entity.common.CommonPersonalFile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 个人文件信息业务模型
 * <p>文件信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
@OpenApiMainTags(
        {
                @OpenApiMainTag("PersonalFileInfo"),
                @OpenApiMainTag(value = {"PersonalFileInfo",
                                         "_PersonalFileInfo"},
                                level = 1)
        })
@EntityMapping(CommonFile.class)
public class PersonalFile
        extends CommonPersonalFile {
    /**
     * 标签集合
     */
    @EntityMappingSetting(ignore = true)
    @OpenApiDescription("标签")
    private java.util.List<String> tagList;

    /**
     * 文件类型
     *
     * @see top.lctr.naive.file.system.dto.FileType
     */
    @OpenApiDescription("文件类型")
    private String fileType;

    /**
     * 内容类型
     */
    @OpenApiDescription("内容类型")
    private String contentType;

    /**
     * MD5校验值
     */
    @OpenApiDescription("MD5校验值")
    private String md5;

    /**
     * 存储类型
     *
     * @see top.lctr.naive.file.system.dto.StorageType
     */
    @OpenApiDescription("存储类型")
    private String storageType;

    /**
     * 字节数
     */
    @OpenApiDescription("字节数")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long bytes;

    /**
     * 文件大小
     */
    @OpenApiDescription("文件大小")
    private String size;

    /**
     * 标签
     */
    public java.util.List<String> getTagList() {
        if (tagList == null)
            setTags(super.getTags());
        return tagList;
    }

    @Override
    public void setTags(String tags) {
        super.setTags(tags);
        this.tagList = StringUtils.hasText(tags)
                       ? Arrays.asList(tags.split(","))
                       : new ArrayList<>();
    }

    /**
     * 文件类型
     *
     * @see top.lctr.naive.file.system.dto.FileType
     */
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 内容类型
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * MD5校验值
     */
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 存储类型
     *
     * @see top.lctr.naive.file.system.dto.StorageType
     */
    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    /**
     * 字节数
     */
    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    /**
     * 文件大小
     */
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
