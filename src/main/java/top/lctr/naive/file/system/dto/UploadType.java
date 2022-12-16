package top.lctr.naive.file.system.dto;

import project.extension.openapi.annotations.OpenApiDescription;

import java.util.Arrays;
import java.util.Optional;

/**
 * 上传类型
 *
 * @author LCTR
 * @date 2022-12-07
 */
public enum UploadType {
    /**
     * 单个图片
     */
    @OpenApiDescription("单个图片")
    SingleImage(0,
                "SingleImage"),
    /**
     * 单个文件
     */
    @OpenApiDescription("单个文件")
    Single(1,
           "Single");

    /**
     * @param index 索引
     * @param value 值
     */
    UploadType(int index,
               String value) {
        this.index = index;
        this.value = value;
    }

    /**
     * 索引
     */
    final int index;

    /**
     * 值
     */
    final String value;

    /**
     * 获取索引
     *
     * @return 索引
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * 获取字符串
     *
     * @return 值
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * 获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static UploadType toEnum(String value)
            throws
            IllegalArgumentException {
        Optional<UploadType> find = Arrays.stream(UploadType.values())
                                          .filter(x -> x.value.equals(value))
                                          .findFirst();
        if (!find.isPresent())
            throw new IllegalArgumentException(String.format("未找到值为%s的%s枚举",
                                                             value,
                                                             UploadType.class.getName()));
        return find.get();
    }

    /**
     * 获取枚举
     *
     * @param index 索引
     * @return 枚举
     */
    public static UploadType toEnum(int index)
            throws
            IllegalArgumentException {
        for (UploadType value : UploadType.values()) {
            if (value.getIndex() == index)
                return value;
        }

        throw new IllegalArgumentException(String.format("指定索引%s无效",
                                                         index));
    }
}
