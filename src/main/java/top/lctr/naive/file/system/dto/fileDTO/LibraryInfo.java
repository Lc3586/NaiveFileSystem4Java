package top.lctr.naive.file.system.dto.fileDTO;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 文件信息业务模型
 * <p>文件库信息</p>
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class LibraryInfo {
    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件总数
     */
    private Integer total;

    /**
     * 文件总字节数
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long bytes;

    /**
     * 文件占用存储空间
     */
    private String size;

    /**
     * 文件类型
     */
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 文件总数
     */
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 文件总字节数
     */
    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    /**
     * 文件占用存储空间
     */
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}