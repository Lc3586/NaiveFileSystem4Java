package top.lctr.naive.file.system.entityFields;

/**
 * 分片文件合并任务对象实体字段
 *
 * @author LCTR
 * @date 2022-12-07
 * @see top.lctr.naive.file.system.entity.CommonChunkFileMergeTask
 */
public class CFMT_Fields
        extends Base_Fields {
    /**
     * Id
     */
    public static final String id = "id";

    /**
     * 服务器标识
     */
    public static final String serverKey = "serverKey";

    /**
     * 文件MD5校验值
     */
    public static final String md5 = "md5";

    /**
     * 名称
     */
    public static final String name = "name";

    /**
     * 内容类型
     */
    public static final String contentType = "contentType";

    /**
     * 文件扩展名
     */
    public static final String extension = "extension";

    /**
     * 字节数
     */
    public static final String bytes = "bytes";

    /**
     * 文件大小
     */
    public static final String size = "size";

    /**
     * 合并后的文件路径
     */
    public static final String path = "path";

    /**
     * 分片规格
     */
    public static final String specs = "specs";

    /**
     * 分片总数
     */
    public static final String total = "total";

    /**
     * 当前处理分片的索引
     */
    public static final String currentChunkIndex = "currentChunkIndex";

    /**
     * 状态（上传中、等待处理、处理中、已完成、失败）
     */
    public static final String state = "state";

    /**
     * 信息
     */
    public static final String info = "info";

    /**
     * 完成时间
     */
    public static final String completedTime = "completedTime";
}
