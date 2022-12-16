package top.lctr.naive.file.system.dto;

import org.springframework.util.StringUtils;

/**
 * 文件类型
 *
 * @author LCTR
 * @date 2022-12-07
 */
public class FileType {
    public static final String 电子文档 = "电子文档";

    public static final String 电子表格 = "电子表格";

    public static final String 文本文件 = "文本文件";

    public static final String 图片 = "图片";

    public static final String 音频 = "音频";

    public static final String 视频 = "视频";

    public static final String 压缩包 = "压缩包";

    public static final String 未知 = "未知";

    public static final String 外链资源 = "外链资源";

    /**
     * 获取文件类型
     *
     * @param extension 文件扩展名(示例：.jpg)
     * @return 文件类型
     */
    public static String getFileTypeByExtension(String extension) {
        switch (extension) {
            case ".webp":
            case ".jpg":
            case ".png":
            case ".ioc":
            case ".bmp":
            case ".gif":
            case ".tif":
            case ".tga":
            case ".jpeg":
                return 图片;
            case ".mp2":
            case ".ac3":
            case ".mp3":
            case ".m4a":
            case ".m4r":
            case ".mmf":
            case ".ogg":
            case ".amr":
            case ".aac":
            case ".vqf":
            case ".wma":
            case ".ape":
            case ".wav":
            case ".flac":
            case ".cda":
            case ".dts":
                return 音频;
            case ".swf":
            case ".3gp":
            case ".3g2":
            case ".mp4":
            case ".mpeg":
            case ".mpg":
            case ".dat":
            case ".mov":
            case ".vob":
            case ".qt":
            case ".rm":
            case ".asf":
            case ".avi":
            case ".navi":
            case ".divx":
            case ".flv":
            case ".f4v":
            case ".qsv":
            case ".wmv":
            case ".mkv":
            case ".rmvb":
            case ".webm":
                return 视频;
            case ".xls":
            case ".xlsx":
            case ".csv":
                return 电子表格;
            case ".pdf":
            case ".doc":
            case ".docx":
                return 电子文档;
            case ".txt":
            case ".js":
            case ".css":
            case ".cs":
            case ".html":
            case ".vue":
            case ".ts":
            case ".xml":
            case ".json":
                return 文本文件;
            case ".zip":
            case ".rar":
            case ".7z":
                return 压缩包;
            default:
                return 未知;
        }
    }

    /**
     * 获取文件类型
     *
     * @param mimetype 内容类型
     * @return 文件类型
     */
    public static String getFileTypeByMIME(String mimetype) {
        if (StringUtils.startsWithIgnoreCase(mimetype,
                                             "image/"))
            return 图片;
        else if (StringUtils.startsWithIgnoreCase(mimetype,
                                                  "audio/"))
            return 音频;
        else if (StringUtils.startsWithIgnoreCase(mimetype,
                                                  "video/"))
            return 视频;
        else if (StringUtils.startsWithIgnoreCase(mimetype,
                                                  "text/"))
            return 文本文件;
        else {
            switch (mimetype) {
                case "application/ogg":
                    return 音频;
                case "application/mp4":
                    return 视频;
                case "application/vnd.ms-excel":
                case "vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    return 电子表格;
                case "application/pdf":
                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    return 电子文档;
                case "application/json":
                case "application/javascript":
                    return 文本文件;
                case "application/x-tar":
                case "application/zip":
                case "application/x-compressed":
                case "application/x-zip-compressed":
                    return 压缩包;
                default:
                    return 未知;
            }
        }
    }
}
