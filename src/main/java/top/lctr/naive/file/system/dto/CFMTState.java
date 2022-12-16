package top.lctr.naive.file.system.dto;

/**
 * 分片文件合并任务状态
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class CFMTState {
    public static final String 上传中 = "上传中";

    public static final String 等待处理 = "等待处理";

    public static final String 处理中 = "处理中";

    public static final String 待清理 = "待清理";

    public static final String 已完成 = "已完成";

    public static final String 失败 = "失败";

    public static final String 已失效 = "已失效";
}
