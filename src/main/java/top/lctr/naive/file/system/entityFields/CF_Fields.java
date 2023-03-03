package top.lctr.naive.file.system.entityFields;

/**
 * 分片文件信息实体字段
 *
 * @author LCTR
 * @date 2022-12-07
 * @see top.lctr.naive.file.system.entity.common.CommonChunkFile
 */
public final class CF_Fields {
    /**
     * Id
     */
    public static final String id = "id";

    /**
     * 服务器标识
     */
    public static final String serverKey = "serverKey";

    /**
     * 任务标识
     */
    public static final String taskKey = "taskKey";

    /**
     * 文件MD5校验值
     */
    public static final String fileMd5 = "fileMd5";

    /**
     * 分片MD5校验值
     */
    public static final String md5 = "md5";

    /**
     * 分片索引
     */
    public static final String index = "index";

    /**
     * 分片规格
     */
    public static final String specs = "specs";

    /**
     * 字节数
     */
    public static final String bytes = "bytes";

    /**
     * 文件大小
     */
    public static final String size = "size";

    /**
     * 文件路径
     */
    public static final String path = "path";
    /**
     * 状态
     */
    public static final String state = "state";

    /**
     * 创建时间
     */
    public static final String createTime = "createTime";

    /**
     * 更新时间
     */
    public static final String updateTime = "updateTime";

}
