package top.lctr.naive.file.system.business.service.Implementation;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.AbsoluteSize;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.geometry.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.extension.date.DateExtension;
import project.extension.file.FileExtension;
import project.extension.file.PathExtension;
import project.extension.file.VideoHelper;
import project.extension.file.VideoInfo;
import project.extension.mybatis.edge.INaiveSql;
import project.extension.mybatis.edge.core.repository.IBaseRepository_Key;
import project.extension.mybatis.edge.extention.RepositoryExtension;
import project.extension.mybatis.edge.model.FilterCompare;
import project.extension.mybatis.edge.model.NullResultException;
import project.extension.office.word.WordHelper;
import project.extension.standard.datasearch.DataSearchDTO;
import project.extension.standard.datasearch.DataSearchExtension;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import project.extension.string.StringExtension;
import project.extension.tuple.Tuple3;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.dto.FileState;
import top.lctr.naive.file.system.dto.FileType;
import top.lctr.naive.file.system.dto.PersonalFileState;
import top.lctr.naive.file.system.dto.StorageType;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.fileDTO.DeleteFunUse_File;
import top.lctr.naive.file.system.dto.fileDTO.FileInfo;
import top.lctr.naive.file.system.dto.fileDTO.LibraryInfo;
import top.lctr.naive.file.system.entity.CommonFile;
import top.lctr.naive.file.system.entity.CommonPersonalFile;
import top.lctr.naive.file.system.entityFields.F_Fields;
import top.lctr.naive.file.system.entityFields.PFI_Fields;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 文件信息服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class FileService
        implements IFileService {
    public FileService(IEntityExtension entityExtension,
                       INaiveSql naiveSql)
            throws
            Throwable {
        this.entityExtension = entityExtension;
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes != null) {
            this.request = servletRequestAttributes.getRequest();
            this.response = servletRequestAttributes.getResponse();
        } else {
//            throw new Exception("获取ServletRequestAttributes对象失败");
            this.request = null;
            this.response = null;
        }
        this.repository_Key = naiveSql.getRepository_Key(CommonFile.class,
                                                         String.class);
        this.repository_Key_PersonalFile = naiveSql.getRepository_Key(CommonPersonalFile.class,
                                                                      String.class);
        this.tableKeyAliasMap = new HashMap<>();
        this.tableKeyAliasMap.put(defaultTableKey,
                                  "a");
    }

    private final IEntityExtension entityExtension;

    private final IBaseRepository_Key<CommonFile, String> repository_Key;

    private final IBaseRepository_Key<CommonPersonalFile, String> repository_Key_PersonalFile;

    /**
     * 日志组件
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 数据表前端标识和别名映射
     */
    private final Map<String, String> tableKeyAliasMap;

    /**
     * 默认的数据库表标识
     */
    private final String defaultTableKey = "main";

    /**
     * 请求对象
     */
    private final HttpServletRequest request;

    /**
     * 响应对象
     */
    private final HttpServletResponse response;

    /**
     * 服务器标识
     */
    @Value("${file.serverKey}")
    private String serverKey;

    /**
     * 站点资源文件根目录绝对路径
     */
    @Value("${file.wwwRootDirectory}")
    private String wwwRootDirectory;

    /**
     * 图片默认预览宽度
     */
    @Value("${file.previewWidth}")
    private Integer filePreviewWidth = 100;

    /**
     * 图片默认预览高度
     */
    @Value("${file.previewHeight}")
    private Integer filePreviewHeight = 100;

    /**
     * ffmpeg程序文件路径
     */
    @Value("${file.ffmpegFilePath}")
    private String ffmpegFilePath;

    /**
     * ffprobe程序文件路径
     */
    @Value("${file.ffprobeFilePath}")
    private String ffprobeFilePath;

    /**
     * 文件状态图存储路径根目录绝对路径
     */
    private String fileStateDirectory() {
        return Paths.get(wwwRootDirectory,
                         "filestate")
                    .toAbsolutePath()
                    .toString();
    }

    /**
     * 文件类型预览图存储路径根目录绝对路径
     */
    private String previewDirectory() {
        return Paths.get(wwwRootDirectory,
                         "filetypes")
                    .toAbsolutePath()
                    .toString();
    }

    /**
     * 检查文件状态并输出错误
     *
     * @param fileState 文件状态
     * @return 是否错误
     */
    private boolean checkFileStateResponseIsError(String fileState)
            throws
            Exception {
        switch (fileState) {
            case FileState.可用:
                return false;
            case FileState.处理中:
            case FileState.已删除:
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       String.format("%s.jpg",
                                                     fileState))
                                  .toString(),
                             "image/jpg");
                return true;
            default:
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "不可用.jpg")
                                  .toString(),
                             "image/jpg");
                return true;
        }
    }

    /**
     * 检查文件状态并抛出错误
     *
     * @param fileState 文件状态
     */
    private void checkFileStateThrowExceptionWhenError(String fileState)
            throws
            BusinessException {
        switch (fileState) {
            case FileState.可用:
                return;
            case FileState.处理中:
                throw new BusinessException("文件还在处理中");
            case FileState.已删除:
                throw new BusinessException("文件已删除");
            default:
                throw new BusinessException("文件不可用");
        }
    }

    /**
     * 获取文件类型图片
     *
     * @param extension 文件拓展名
     * @return 图片绝对路径
     */
    private String getFileTypeImage(String extension) {
        if (StringUtils.hasText(extension)) {
            String fileTypeFile = Paths.get(previewDirectory(),
                                            String.format("%s.png",
                                                          extension.indexOf(".") == 0
                                                          ? extension.substring(1)
                                                          : extension))
                                       .toString();
            if (new File(fileTypeFile).exists())
                return fileTypeFile;
        }

        return Paths.get(previewDirectory(),
                         "empty.png")
                    .toString();
    }

    /**
     * 检查并设置范围
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param bytes    总字节数
     * @return <是否设置了范围, 起始字节索引, 结束字节索引>
     */
    private static Tuple3<Boolean, Long, Long> checkAndSetRange(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                                Long bytes) {
        String rangeText = request.getHeader("Range");

        Long count = bytes;

        if (StringUtils.hasText(rangeText)) {
            rangeText = rangeText.replace("bytes=",
                                          "");
            String[] rangeValue = rangeText.split("-");

            Long start = Long.parseLong(rangeValue[0]);
            Long end = null;

            if (rangeValue.length > 1) {
                end = Long.parseLong(rangeValue[1]);
                if (end.compareTo(start) <= 0) end = bytes;

                count = end - start;
            }

            if (count.compareTo(0L) == 0 || start + count > bytes) count = bytes - start;

            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            response.setHeader("Accept-Ranges",
                               "bytes");
            response.setHeader("Content-Range",
                               String.format("bytes %s-%s/%s",
                                             start,
                                             start + count - 1,
                                             bytes));
            response.setContentLengthLong(count);

            return new Tuple3<>(true,
                                start,
                                end);
        } else {
            response.setStatus(HttpStatus.OK.value());
            response.setContentLengthLong(count);
            return new Tuple3<>(false,
                                null,
                                null);
        }
    }

    /**
     * 获取图片缩略图
     * <p>创建缩略图后会存储在本地</p>
     *
     * @param path   图片文件路径
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @param region 裁剪图片（null值表示不裁剪）
     * @return 缩略图文件路径
     */
    private static String getImageThumbnail(String path,
                                            int width,
                                            int height,
                                            @Nullable
                                                    Region region)
            throws
            Exception {
        //文件拓展名
        String extension = PathExtension.getExtension(path);

        //存储缩略图的文件夹
        File thumbnailsDir = new File(String.format("%s-Thumbnails",
                                                    PathExtension.trimExtension(path,
                                                                                extension)));

        if (!thumbnailsDir.exists()) {
            if (!thumbnailsDir.mkdir())
                throw new Exception(String.format("创建文件夹失败, %s",
                                                  thumbnailsDir.getPath()));
        }

        //缩略图文件存储路径
        String imagePath = Paths.get(thumbnailsDir.getAbsolutePath(),
                                     String.format("%spx_%spx_%s_%s%s",
                                                   width,
                                                   height,
                                                   region == null
                                                   ? "0"
                                                   : region.getPosition(),
                                                   region == null
                                                   ? "0"
                                                   : region.getSize(),
                                                   extension))
                                .toString();

        if (!new File(imagePath).exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(path)) {
                BufferedImage originalImage = ImageIO.read(fileInputStream);
                Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(originalImage);

                //裁剪
                if (region != null)
                    builder.sourceRegion(region);

                //缩放或放大
                builder.size(width,
                             height);

                builder.toFile(imagePath);
            } catch (Exception ex) {
                throw new Exception("创建缩略图失败",
                                    ex);
            }
        }

        return imagePath;
    }

    /**
     * 获取视频截图
     * <p>创建截图后会存储在本地</p>
     *
     * @param ffmpegFilePath ffmpeg应用程序文件路径
     * @param path           视频文件路径
     * @param width          截图宽度
     * @param height         截图高度
     * @param time           时间轴
     * @return 截图文件路径
     */
    private static String getVideoScreenshot(String ffmpegFilePath,
                                             String path,
                                             int width,
                                             int height,
                                             String time)
            throws
            Exception {
        //存储截图的文件夹
        File screenshotDir = new File(String.format("%s-Screenshot",
                                                    PathExtension.trimExtension(path)));

        if (!screenshotDir.exists()) {
            if (!screenshotDir.mkdir())
                throw new Exception(String.format("创建文件夹失败, %s",
                                                  screenshotDir.getPath()));
        }

        //截图图文件存储路径
        String imagePath = Paths.get(screenshotDir.getAbsolutePath(),
                                     String.format("%s-%spxx%spx.jpg",
                                                   time.replaceAll("[.]",
                                                                   "_")
                                                       .replaceAll("[:]",
                                                                   "_"),
                                                   width,
                                                   height))
                                .toAbsolutePath()
                                .toString();

        if (!new File(imagePath).exists()) {
            try {
                //视频截图
                VideoHelper.screenshot(path,
                                       Paths.get(ffmpegFilePath)
                                            .toAbsolutePath()
                                            .toString(),
                                       imagePath,
                                       //转为毫秒数防止恶意代码注入
                                       String.format("%sms",
                                                     DateExtension.getTimeMilliseconds(time)
                                                                  .toString()),
                                       width,
                                       height);
            } catch (Exception ex) {
                throw new Exception("视频截图失败",
                                    ex);
            }
        }

        return imagePath;
    }

    /**
     * 输出文件
     *
     * @param response    响应对象
     * @param file        文件
     * @param contentType 内容类型
     */
    private static void responseFile(HttpServletResponse response,
                                     File file,
                                     String contentType)
            throws
            Exception {
        responseFile(response,
                     file,
                     contentType,
                     null,
                     null);
    }

    /**
     * 输出文件
     *
     * @param response    响应对象
     * @param file        文件
     * @param contentType 内容类型
     * @param rangeStart  起始字节索引
     * @param rangeEnd    结束字节索引
     */
    private static void responseFile(HttpServletResponse response,
                                     File file,
                                     String contentType,
                                     Long rangeStart,
                                     Long rangeEnd)
            throws
            Exception {
        response.setContentType(StringUtils.hasText(contentType)
                                ? contentType
                                : "application/octet-stream");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                if (rangeStart != null) {
                    rangeStart = inputStream.skip(rangeStart);
                    if (rangeEnd != null)
                        rangeEnd -= rangeStart;
                }

                int buffSize = 1024;
                byte[] buff = new byte[buffSize];

                while (true) {
                    int length = rangeEnd == null || rangeEnd + 1 - buffSize > 0
                                 ? buffSize
                                 : rangeEnd.intValue() + 1;
                    if (rangeEnd != null) {
                        rangeEnd -= length;
                        if (rangeEnd <= 0)
                            break;
                    }

                    int bytesSize = inputStream.read(buff,
                                                     0,
                                                     length);
                    if (bytesSize <= 0)
                        break;

                    outputStream.write(buff,
                                       0,
                                       bytesSize);
                }

                outputStream.flush();
            }
        }
    }

    /**
     * 获取文件类型
     *
     * @param contentType 内容类型
     * @param extension   文件拓展名
     * @return 文件类型
     */
    private String getFileType(String contentType,
                               String extension) {
        String fileType = FileType.未知;
        //先使用文件内容类型获取文件类型
        if (StringUtils.hasText(contentType))
            fileType = FileType.getFileTypeByMIME(contentType);
        //如果未获取到则再尝试使用文件拓展名获取文件类型
        if (fileType.equals(FileType.未知) && StringUtils.hasText(extension))
            fileType = FileType.getFileTypeByExtension(extension);
        return fileType;
    }

    /**
     * 输出信息
     *
     * @param response   Http响应对象
     * @param httpStatus 状态码
     * @param message    消息
     */
    public static void response(HttpServletResponse response,
                                HttpStatus httpStatus,
                                String message) {
        response.setStatus(httpStatus.value());
        response.setHeader("content-type",
                           "text/plan;charset=UTF-8");
        try {
            response.getWriter()
                    .println(message);
        } catch (IOException ignored) {

        }
    }

    /**
     * 输出文件
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param path        文件路径
     * @param contentType 内容类型
     */
    public static void responseFile(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String path,
                                    String contentType)
            throws
            Exception {
        responseFile(request,
                     response,
                     path,
                     null,
                     contentType);
    }

    /**
     * 输出文件
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param path        文件路径
     * @param bytes       文件字节数
     * @param contentType 内容类型
     */
    public static void responseFile(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String path,
                                    Long bytes,
                                    String contentType)
            throws
            Exception {
        File file = new File(path);
        if (!file.exists()) {
            response(response,
                     HttpStatus.NOT_FOUND,
                     "文件不存在或已被删除");
            return;
        }

        if (bytes == null)
            bytes = file.length();

        Tuple3<Boolean, Long, Long> rangeSetting = checkAndSetRange(request,
                                                                    response,
                                                                    bytes);

        if (rangeSetting.a)
            responseFile(response,
                         file,
                         contentType,
                         rangeSetting.b,
                         rangeSetting.c);
        else
            responseFile(response,
                         file,
                         contentType);
    }

    /**
     * Word转换为Pdf
     *
     * @param id                Word文件主键
     * @param withTransactional 事务
     * @return Pdf文件主键
     */
    private String word2PdfReturnId(String id,
                                    boolean withTransactional) {
        try {
            FileInfo fileInfo = repository_Key.withTransactional(withTransactional)
                                              .getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");

            checkFileStateThrowExceptionWhenError(fileInfo.getState());

            if ((!StringExtension.ignoreCaseEquals(fileInfo.getExtension(),
                                                   ".doc")
                    && !StringExtension.ignoreCaseEquals(fileInfo.getExtension(),
                                                         ".docx"))
                    || !fileInfo.getStorageType()
                                .equals(StorageType.相对路径))
                throw new BusinessException("无法将此文件转换为PDF文件");

            String wordFilePath = getFilePath(fileInfo.getPath());

            String pdfPath = String.format("%s.pdf",
                                           PathExtension.trimExtension(fileInfo.getPath(),
                                                                       fileInfo.getExtension()));

            String savePath = getFilePath(pdfPath);

            //PDF文件是否已存在
            String history_id = repository_Key.select()
                                              .columns(F_Fields.id)
                                              .where(x -> x.and(F_Fields.path,
                                                                FilterCompare.Eq,
                                                                pdfPath)
                                                           .and(F_Fields.storageType,
                                                                FilterCompare.Eq,
                                                                StorageType.相对路径)
                                                           .and(F_Fields.state,
                                                                FilterCompare.Eq,
                                                                FileState.可用))
                                              .first(String.class);

            if (StringUtils.hasText(history_id)) {
                if (new File(savePath).exists())
                    return history_id;

                if (repository_Key.updateDiy()
                                  .set(F_Fields.state,
                                       FileState.已删除)
                                  .where(x -> x.and(F_Fields.id,
                                                    FilterCompare.Eq,
                                                    history_id))
                                  .executeAffrows() < 0)
                    throw new Exception("清理已失效的文件失败");
            }

            WordHelper.convert2Pdf(wordFilePath,
                                   savePath,
                                   WordHelper.ConvertTool.Jacob);

            CommonFile file = create(FileExtension.md5(wordFilePath),
                                     fileInfo.getName(),
                                     ".pdf",
                                     Files.probeContentType(Paths.get("just4name.pdf")),
                                     new File(savePath).length(),
                                     pdfPath,
                                     StorageType.相对路径,
                                     fileInfo.getState(),
                                     withTransactional);

            return file.getId();
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage(),
                                        ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    public List<FileInfo> list(DataSearchDTO dataSearch)
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .as(tableKeyAliasMap.get(defaultTableKey))
                                 .where(x -> x.and(DataSearchExtension.toDynamicFilter(dataSearch.getFilters(),
                                                                                       tableKeyAliasMap)))
                                 .orderBy(x -> dataSearch.getOrder() == null
                                               ? x.orderBy(
                                         F_Fields.createTime)
                                               : x.orderBy(DataSearchExtension.toDynamicOrder(dataSearch.getOrder(),
                                                                                              tableKeyAliasMap)))
                                 .pagination(dataSearch.getPagination())
                                 .toList(FileInfo.class);
        } catch (Exception ex) {
            throw new BusinessException("查询数据失败",
                                        ex);
        }
    }

    @Override
    public List<String> getUnrepairedIdList(boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .columns(F_Fields.id)
                             .where(x -> x.and(F_Fields.state,
                                               FilterCompare.Eq,
                                               FileState.待修复)
                                          .and(F_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .toList(String.class);
    }

    @Override
    public List<String> getUnConvert2PdfIdList()
            throws
            Exception {
        return repository_Key.select()
                             .withSql("select * from \"COMMON_FILE\" where \"NAME\" in (select ii.\"NAME\" from (select i.\"NAME\", count(1) as \"C\" from \"COMMON_FILE\" as i group by i.\"NAME\") as ii where ii.\"C\"=1)")
                             .where(x -> x.and(y -> y.and(F_Fields.extension,
                                                          FilterCompare.EqIgnoreCase,
                                                          ".doc")
                                                     .or(F_Fields.extension,
                                                         FilterCompare.EqIgnoreCase,
                                                         ".docx"))
                                          .and(F_Fields.storageType,
                                               FilterCompare.Eq,
                                               StorageType.相对路径)
                                          .and(F_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .columns(F_Fields.id)
                             .toList(String.class);
    }

    @Override
    public FileInfo detail(String id)
            throws
            BusinessException {
        try {
            return repository_Key.getByIdAndCheckNull(id,
                                                      FileInfo.class);
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage());
        } catch (Throwable ex) {
            throw new BusinessException("获取详情数据失败",
                                        ex);
        }
    }

    @Override
    public List<FileInfo> detailList(Collection<String> ids)
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .as(tableKeyAliasMap.get(defaultTableKey))
                                 .where(x -> x.and(F_Fields.id,
                                                   FilterCompare.InSet,
                                                   ids))
                                 .toList(FileInfo.class);
        } catch (Exception ex) {
            throw new BusinessException("获取详情数据集合失败",
                                        ex);
        }
    }

    @Override
    public FunUse_FileState getFileState(String md5,
                                         boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(F_Fields.md5,
                                               FilterCompare.Eq,
                                               md5)
                                          .and(F_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .orderBy(String.format("(CASE WHEN state='%s' THEN 1 ELSE 0 END) DESC, (CASE WHEN state='%s' THEN 1 ELSE 0 END) DESC, (CASE WHEN state='%s' THEN 1 ELSE 0 END) DESC",
                                                    FileState.可用,
                                                    FileState.处理中,
                                                    FileState.上传中))
                             .first(FunUse_FileState.class);
    }

    @Override
    public void updateFileState(String md5,
                                String fileState,
                                String path,
                                boolean withTransactional)
            throws
            Exception {
        if (repository_Key.withTransactional(withTransactional)
                          .updateDiy()
                          .set(F_Fields.state,
                               fileState)
                          .set(F_Fields.path,
                               path)
                          .where(x -> x.and(F_Fields.md5,
                                            FilterCompare.Eq,
                                            md5)
                                       .and(F_Fields.serverKey,
                                            FilterCompare.Eq,
                                            serverKey))
                          .executeAffrows() < 0)
            throw new BusinessException("更新文件状态信息失败");
    }

    @Override
    public CommonFile get(String id,
                          boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(F_Fields.id,
                                               FilterCompare.Eq,
                                               id))
                             .first();
    }

    @Override
    public void update(CommonFile file,
                       boolean withTransactional)
            throws
            Exception {
        repository_Key.withTransactional(withTransactional)
                      .update(file);
    }

    @Override
    public List<String> getRepairIdList(boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .columns(F_Fields.id)
                             .where(x -> x.and(F_Fields.state,
                                               FilterCompare.Eq,
                                               FileState.待修复)
                                          .and(F_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .toList(String.class);
    }

    @Override
    public CommonFile create(
            String md5,
            String name,
            String extension,
            String contentType,
            Long bytes,
            String relativePath,
            String storageType,
            String state,
            boolean withTransactional)
            throws
            BusinessException {
        try {
            CommonFile data = new CommonFile();
            data.setServerKey(serverKey);
            data.setMd5(md5);
            data.setName(name);
            data.setExtension(extension);
            data.setContentType(contentType);
            data.setFileType(getFileType(data.getContentType(),
                                         data.getExtension()));
            data.setBytes(bytes);
            data.setSize(FileExtension.getFileSize(bytes));
            data.setPath(relativePath);
            data.setStorageType(storageType);
            data.setState(state);
            repository_Key.withTransactional(withTransactional)
                          .insert(entityExtension.initialization(data));
            return data;
        } catch (Exception ex) {
            throw new BusinessException("新增文件信息失败",
                                        ex);
        }
    }

    @Override
    public void update(String md5,
                       String name,
                       String extension,
                       String contentType,
                       Long bytes,
                       String relativePath,
                       String storageType,
                       String state,
                       boolean doNotUpdateAvailableFile,
                       boolean withTransactional)
            throws
            BusinessException {
        try {
            if (repository_Key.withTransactional(withTransactional)
                              .updateDiy()
                              .set(F_Fields.state,
                                   state)
                              .set(F_Fields.name,
                                   name)
                              .set(F_Fields.extension,
                                   extension)
                              .set(F_Fields.contentType,
                                   contentType)
                              .set(F_Fields.path,
                                   relativePath)
                              .set(F_Fields.bytes,
                                   bytes)
                              .set(F_Fields.size,
                                   FileExtension.getFileSize(bytes))
                              .set(F_Fields.storageType,
                                   storageType)
                              .set(F_Fields.fileType,
                                   getFileType(contentType,
                                               extension))
                              .where(x -> {
                                  x.and(F_Fields.md5,
                                        FilterCompare.Eq,
                                        md5)
                                   .and(F_Fields.serverKey,
                                        FilterCompare.Eq,
                                        serverKey);
                                  if (doNotUpdateAvailableFile)
                                      x.and(F_Fields.state,
                                            FilterCompare.NotEq,
                                            state);
                                  return x;
                              })
                              .executeAffrows() < 0)
                throw new BusinessException("更新文件信息失败");
        } catch (Exception ex) {
            throw new BusinessException("更新文件信息失败",
                                        ex);
        }
    }

    @Override
    public void delete(Collection<String> ids,
                       boolean withTransactional)
            throws
            BusinessException {
        try {
            List<DeleteFunUse_File> files = repository_Key.withTransactional(withTransactional)
                                                          .select()
                                                          .where(x -> x.and(F_Fields.id,
                                                                            FilterCompare.InSet,
                                                                            ids))
                                                          .toList(DeleteFunUse_File.class);

            for (DeleteFunUse_File file : files) {
                if (file.getStorageType()
                        .equals(StorageType.相对路径) && StringUtils.hasText(file.getPath())) {
                    //绝对路径
                    String path = getFilePath(file.getPath());

                    if (!FileExtension.delete(path))
                        throw new BusinessException("删除文件失败");

                    String screenshot = String.format("%s-Screenshot",
                                                      PathExtension.trimExtension(path));
                    if (!FileExtension.delete(screenshot))
                        throw new BusinessException("删除文件缓存失败");
                }

                if (repository_Key.updateDiy()
                                  .set(F_Fields.state,
                                       FileState.已删除)
                                  .where(x -> x.and(F_Fields.id,
                                                    FilterCompare.Eq,
                                                    file.getId()))
                                  .executeAffrows() < 0)
                    throw new BusinessException("更新文件信息失败");

                if (repository_Key_PersonalFile.withTransactional(withTransactional)
                                               .updateDiy()
                                               .set(PFI_Fields.state,
                                                    PersonalFileState.已删除)
                                               .where(x -> x.and(PFI_Fields.fileId,
                                                                 FilterCompare.Eq,
                                                                 file.getId()))
                                               .executeAffrows() < 0)
                    throw new BusinessException("更新个人文件信息失败");
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("删除失败",
                                        ex);
        }
    }

    @Override
    public void preview(String id,
                        Integer width,
                        Integer height,
                        String time)
            throws
            BusinessException {
        if (width == null)
            width = filePreviewWidth;
        if (height == null)
            height = filePreviewHeight;

        try {
            FileInfo fileInfo = repository_Key.getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.外链地址:
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.相对路径:
                    //可以执行预览的流程
                    break;
                default:
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "不支持预览.jpg")
                                      .toString(),
                                 "image/jpg");
                    return;
            }

            //绝对路径
            String path = getFilePath(fileInfo.getPath());
            File file = new File(path);
            if (!file.exists())
                throw new NullResultException("文件不存在或已被删除");

            String imagePath;
            String contentType;
            switch (fileInfo.getFileType()) {
                case FileType.图片:
                    imagePath = getImageThumbnail(path,
                                                  width,
                                                  height,
                                                  null);
                    contentType = fileInfo.getContentType();
                    break;
                case FileType.视频:
                    if (!StringUtils.hasText(time))
                        time = "00:00:00.001";

                    imagePath = getVideoScreenshot(ffmpegFilePath,
                                                   path,
                                                   width,
                                                   height,
                                                   time);
                    contentType = "image/jpg";
                    break;
                default:
                    imagePath = getImageThumbnail(getFileTypeImage(fileInfo.getExtension()),
                                                  width,
                                                  height,
                                                  null);
                    contentType = "image/png";
            }

            responseFile(request,
                         response,
                         imagePath,
                         contentType);
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                responseFile(request,
                             response,
                             getImageThumbnail(Paths.get(fileStateDirectory(),
                                                         "不存在或已被删除.jpg")
                                                    .toString(),
                                               width,
                                               height,
                                               new Region(Positions.CENTER,
                                                          new AbsoluteSize(400,
                                                                           255))),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.NOT_FOUND,
                         "文件不存在或已被删除");
            }
        } catch (Exception ex) {
            logger.error("预览失败",
                         ex);
            try {
                responseFile(request,
                             response,
                             getImageThumbnail(Paths.get(fileStateDirectory(),
                                                         "处理失败.jpg")
                                                    .toString(),
                                               width,
                                               height,
                                               new Region(Positions.CENTER,
                                                          new AbsoluteSize(400,
                                                                           255))),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.INTERNAL_SERVER_ERROR,
                         "预览失败");
            }
        }
    }

    @Override
    @Transactional
    public void browse(String id)
            throws
            BusinessException {
        try {
            FileInfo fileInfo = repository_Key.withTransactional(true)
                                              .getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.外链地址:
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.相对路径:
                    if ("application/msword".equals(fileInfo.getContentType())
                            || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileInfo.getContentType())
                            || ".doc".equals(fileInfo.getExtension())
                            || ".docx".equals(fileInfo.getExtension())) {
                        //word文件转pdf后再浏览
                        AtomicReference<FileInfo> pdfFileInfo = new AtomicReference<>();
                        pdfFileInfo.set(word2Pdf(fileInfo.getId(),
                                                 true));

                        //可以浏览
                        responseFile(request,
                                     response,
                                     getFilePath(pdfFileInfo.get()
                                                            .getPath()),
                                     pdfFileInfo.get()
                                                .getBytes(),
                                     pdfFileInfo.get()
                                                .getContentType());
                        break;
                    } else if (Arrays.stream(new String[]{FileType.图片,
                                                          FileType.视频,
                                                          FileType.文本文件})
                                     .anyMatch(x -> x.equals(fileInfo.getFileType()))
                            || "application/pdf".equals(fileInfo.getContentType())
                            || ".pdf".equals(fileInfo.getExtension())) {
                        //可以浏览
                        responseFile(request,
                                     response,
                                     getFilePath(fileInfo.getPath()),
                                     fileInfo.getBytes(),
                                     fileInfo.getContentType());
                        break;
                    }
                default:
                    //无法预览
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "不支持浏览.jpg")
                                      .toString(),
                                 "image/jpg");
                    break;
            }
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "不存在或已被删除.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.NOT_FOUND,
                         "文件不存在或已被删除");
            }
        } catch (Exception ex) {
            logger.error("浏览失败",
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "处理失败.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.INTERNAL_SERVER_ERROR,
                         "浏览失败");
            }
        }
    }

    @Override
    public void download(String id,
                         String rename)
            throws
            BusinessException {
        try {
            FileInfo fileInfo = repository_Key.getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.外链地址:
                    //直接重定向
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.相对路径:
                    //可以下载
                    response.setHeader("Content-Disposition",
                                       String.format("attachment; filename=\"%s%s\"",
                                                     URLEncoder.encode(StringUtils.hasText(rename)
                                                                       ? rename
                                                                       : fileInfo.getName(),
                                                                       StandardCharsets.UTF_8.name()),
                                                     fileInfo.getExtension()));
                    responseFile(request,
                                 response,
                                 getFilePath(fileInfo.getPath()),
                                 fileInfo.getBytes(),
                                 fileInfo.getContentType());
                    break;
                default:
                    //无法下载
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "不支持下载.jpg")
                                      .toString(),
                                 "image/jpg");
                    break;
            }
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "不存在或已被删除.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.NOT_FOUND,
                         "文件不存在或已被删除");
            }
        } catch (Exception ex) {
            logger.error("下载失败",
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "处理失败.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.INTERNAL_SERVER_ERROR,
                         "下载失败");
            }
        }
    }

    @Override
    public void save(String id,
                     String saveDirPath,
                     String rename)
            throws
            BusinessException {
        try {
            FileInfo fileInfo = repository_Key.getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");

            checkFileStateThrowExceptionWhenError(fileInfo.getState());

            String savePath = Paths.get(saveDirPath,
                                        String.format("%s%s",
                                                      StringUtils.hasText(rename)
                                                      ? rename
                                                      : fileInfo.getName(),
                                                      fileInfo.getExtension()))
                                   .toAbsolutePath()
                                   .toString();

            switch (fileInfo.getStorageType()) {
                case StorageType.外链地址:
                    //下载文件
                    FileExtension.download(fileInfo.getPath(),
                                           savePath);
                    break;
                case StorageType.相对路径:
                    //复制文件
                    FileExtension.copy(getFilePath(fileInfo.getPath()),
                                       savePath);
                    break;
                default:
                    //无法下载
                    throw new BusinessException("此文件不支持下载");
            }
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage(),
                                        ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    public String fileTypeByExtension(String extension)
            throws
            BusinessException {
        return FileType.getFileTypeByExtension(extension.indexOf(".") == 0
                                               ? extension
                                               : String.format(".%s",
                                                               extension));
    }

    @Override
    public String fileTypeByMIME(String mime)
            throws
            BusinessException {
        return FileType.getFileTypeByMIME(mime.toLowerCase(Locale.ROOT));
    }

    @Override
    public void fileTypeImage(String extension)
            throws
            BusinessException {
        try {
            responseFile(request,
                         response,
                         getFileTypeImage(extension.toLowerCase(Locale.ROOT)),
                         "image/png");
        } catch (Exception ex) {
            response(response,
                     HttpStatus.INTERNAL_SERVER_ERROR,
                     "获取失败");
        }
    }

    @Override
    public String fileSize(String length)
            throws
            BusinessException {
        return FileExtension.getFileSize(Long.parseLong(length));
    }

    @Override
    public VideoInfo videoInfo(String id,
                               Boolean format,
                               Boolean streams,
                               Boolean chapters,
                               Boolean programs,
                               Boolean version)
            throws
            BusinessException {
        try {
            FileInfo fileInfo = repository_Key.getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "文件不存在或已被删除");
            checkFileStateThrowExceptionWhenError(fileInfo.getState());

            if (!fileInfo.getFileType()
                         .equals(FileType.视频))
                throw new BusinessException("指定文件必须是受支持的视频文件");

            if (!fileInfo.getStorageType()
                         .equals(StorageType.相对路径))
                throw new BusinessException("指定文件未存储在本地服务器上");

            //获取视频信息
            return VideoHelper.getVideoInfo(getFilePath(fileInfo.getPath()),
                                            ffprobeFilePath,
                                            format,
                                            streams,
                                            chapters,
                                            programs,
                                            version);
        } catch (BusinessException ex) {
            throw ex;
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage(),
                                        ex);
        } catch (Exception ex) {
            throw new BusinessException("获取视频信息失败",
                                        ex);
        }
    }

    @Override
    public List<LibraryInfo> libraryInfo()
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .withSql(String.format("SELECT file_type as FileType, COUNT(1), SUM(bytes) FROM common_file GROUP BY file_type HAVING state = '%s'",
                                                        FileState.可用))
                                 .toList(LibraryInfo.class);
//            repository_Key.select()
//                    .where(x -> x.and(CFFields.state, FilterCompare.Eq, FileState.可用))
//                    .groupBy(x->)
        } catch (Exception ex) {
            throw new BusinessException("获取文件库信息失败",
                                        ex);
        }
    }

    @Override
    public List<String> fileTypes()
            throws
            BusinessException {
        return Arrays.stream(FileType.class.getDeclaredFields())
                     .map(x -> {
                         try {
                             return (String) x.get(null);
                         } catch (IllegalAccessException e) {
                             return null;
                         }
                     })
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    @Override
    public List<String> storageTypes()
            throws
            BusinessException {
        return Arrays.stream(StorageType.class.getDeclaredFields())
                     .map(x -> {
                         try {
                             return (String) x.get(null);
                         } catch (IllegalAccessException e) {
                             return null;
                         }
                     })
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    @Override
    public List<String> fileStates()
            throws
            BusinessException {
        return Arrays.stream(FileState.class.getDeclaredFields())
                     .map(x -> {
                         try {
                             return (String) x.get(null);
                         } catch (IllegalAccessException e) {
                             return null;
                         }
                     })
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    @Override
    public String getWWWRootDirectory() {
        return wwwRootDirectory;
    }

    @Override
    public String getFileStateDirectory() {
        return fileStateDirectory();
    }

    @Override
    public String getFilePathById(String id) {
        try {
            Map<String, Object> info = repository_Key.select()
                                                     .columns(F_Fields.state,
                                                              F_Fields.path)
                                                     .where(x -> x.and(F_Fields.id,
                                                                       FilterCompare.Eq,
                                                                       id))
                                                     .firstMap();
            if (info == null)
                throw new BusinessException("文件不存在或已被移除");

            checkFileStateThrowExceptionWhenError((String) RepositoryExtension.getMapValueByFieldName(info,
                                                                                                      F_Fields.state));

            return getFilePath((String) RepositoryExtension.getMapValueByFieldName(info,
                                                                                   F_Fields.path));
        } catch (Exception ex) {
            throw new BusinessException("获取文件路径失败",
                                        ex);
        }
    }

    @Override
    public String getFilePath(String path) {
        return Paths.get(getWWWRootDirectory(),
                         path)
                    .toAbsolutePath()
                    .toString();
    }

    @Override
    public FileInfo word2Pdf(String id,
                             boolean withTransactional)
            throws
            BusinessException {
        return detail(word2PdfReturnId(id,
                                       withTransactional));
    }

    @Override
    public void word2Pdf(String id)
            throws
            BusinessException {
        word2PdfReturnId(id,
                         false);
    }
}
