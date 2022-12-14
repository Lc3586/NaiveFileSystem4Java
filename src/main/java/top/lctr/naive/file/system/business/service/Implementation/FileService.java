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
 * ??????????????????
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
//            throw new Exception("??????ServletRequestAttributes????????????");
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
     * ????????????
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * ????????????????????????????????????
     */
    private final Map<String, String> tableKeyAliasMap;

    /**
     * ???????????????????????????
     */
    private final String defaultTableKey = "main";

    /**
     * ????????????
     */
    private final HttpServletRequest request;

    /**
     * ????????????
     */
    private final HttpServletResponse response;

    /**
     * ???????????????
     */
    @Value("${file.serverKey}")
    private String serverKey;

    /**
     * ???????????????????????????????????????
     */
    @Value("${file.wwwRootDirectory}")
    private String wwwRootDirectory;

    /**
     * ????????????????????????
     */
    @Value("${file.previewWidth}")
    private Integer filePreviewWidth = 100;

    /**
     * ????????????????????????
     */
    @Value("${file.previewHeight}")
    private Integer filePreviewHeight = 100;

    /**
     * ffmpeg??????????????????
     */
    @Value("${file.ffmpegFilePath}")
    private String ffmpegFilePath;

    /**
     * ffprobe??????????????????
     */
    @Value("${file.ffprobeFilePath}")
    private String ffprobeFilePath;

    /**
     * ????????????????????????????????????????????????
     */
    private String fileStateDirectory() {
        return Paths.get(wwwRootDirectory,
                         "filestate")
                    .toAbsolutePath()
                    .toString();
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    private String previewDirectory() {
        return Paths.get(wwwRootDirectory,
                         "filetypes")
                    .toAbsolutePath()
                    .toString();
    }

    /**
     * ?????????????????????????????????
     *
     * @param fileState ????????????
     * @return ????????????
     */
    private boolean checkFileStateResponseIsError(String fileState)
            throws
            Exception {
        switch (fileState) {
            case FileState.??????:
                return false;
            case FileState.?????????:
            case FileState.?????????:
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
                                       "?????????.jpg")
                                  .toString(),
                             "image/jpg");
                return true;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param fileState ????????????
     */
    private void checkFileStateThrowExceptionWhenError(String fileState)
            throws
            BusinessException {
        switch (fileState) {
            case FileState.??????:
                return;
            case FileState.?????????:
                throw new BusinessException("?????????????????????");
            case FileState.?????????:
                throw new BusinessException("???????????????");
            default:
                throw new BusinessException("???????????????");
        }
    }

    /**
     * ????????????????????????
     *
     * @param extension ???????????????
     * @return ??????????????????
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
     * ?????????????????????
     *
     * @param request  ????????????
     * @param response ????????????
     * @param bytes    ????????????
     * @return <?????????????????????, ??????????????????, ??????????????????>
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
     * ?????????????????????
     * <p>????????????????????????????????????</p>
     *
     * @param path   ??????????????????
     * @param width  ???????????????
     * @param height ???????????????
     * @param region ???????????????null?????????????????????
     * @return ?????????????????????
     */
    private static String getImageThumbnail(String path,
                                            int width,
                                            int height,
                                            @Nullable
                                                    Region region)
            throws
            Exception {
        //???????????????
        String extension = PathExtension.getExtension(path);

        //???????????????????????????
        File thumbnailsDir = new File(String.format("%s-Thumbnails",
                                                    PathExtension.trimExtension(path,
                                                                                extension)));

        if (!thumbnailsDir.exists()) {
            if (!thumbnailsDir.mkdir())
                throw new Exception(String.format("?????????????????????, %s",
                                                  thumbnailsDir.getPath()));
        }

        //???????????????????????????
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

                //??????
                if (region != null)
                    builder.sourceRegion(region);

                //???????????????
                builder.size(width,
                             height);

                builder.toFile(imagePath);
            } catch (Exception ex) {
                throw new Exception("?????????????????????",
                                    ex);
            }
        }

        return imagePath;
    }

    /**
     * ??????????????????
     * <p>?????????????????????????????????</p>
     *
     * @param ffmpegFilePath ffmpeg????????????????????????
     * @param path           ??????????????????
     * @param width          ????????????
     * @param height         ????????????
     * @param time           ?????????
     * @return ??????????????????
     */
    private static String getVideoScreenshot(String ffmpegFilePath,
                                             String path,
                                             int width,
                                             int height,
                                             String time)
            throws
            Exception {
        //????????????????????????
        File screenshotDir = new File(String.format("%s-Screenshot",
                                                    PathExtension.trimExtension(path)));

        if (!screenshotDir.exists()) {
            if (!screenshotDir.mkdir())
                throw new Exception(String.format("?????????????????????, %s",
                                                  screenshotDir.getPath()));
        }

        //???????????????????????????
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
                //????????????
                VideoHelper.screenshot(path,
                                       Paths.get(ffmpegFilePath)
                                            .toAbsolutePath()
                                            .toString(),
                                       imagePath,
                                       //???????????????????????????????????????
                                       String.format("%sms",
                                                     DateExtension.getTimeMilliseconds(time)
                                                                  .toString()),
                                       width,
                                       height);
            } catch (Exception ex) {
                throw new Exception("??????????????????",
                                    ex);
            }
        }

        return imagePath;
    }

    /**
     * ????????????
     *
     * @param response    ????????????
     * @param file        ??????
     * @param contentType ????????????
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
     * ????????????
     *
     * @param response    ????????????
     * @param file        ??????
     * @param contentType ????????????
     * @param rangeStart  ??????????????????
     * @param rangeEnd    ??????????????????
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
     * ??????????????????
     *
     * @param contentType ????????????
     * @param extension   ???????????????
     * @return ????????????
     */
    private String getFileType(String contentType,
                               String extension) {
        String fileType = FileType.??????;
        //?????????????????????????????????????????????
        if (StringUtils.hasText(contentType))
            fileType = FileType.getFileTypeByMIME(contentType);
        //?????????????????????????????????????????????????????????????????????
        if (fileType.equals(FileType.??????) && StringUtils.hasText(extension))
            fileType = FileType.getFileTypeByExtension(extension);
        return fileType;
    }

    /**
     * ????????????
     *
     * @param response   Http????????????
     * @param httpStatus ?????????
     * @param message    ??????
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
     * ????????????
     *
     * @param request     ????????????
     * @param response    ????????????
     * @param path        ????????????
     * @param contentType ????????????
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
     * ????????????
     *
     * @param request     ????????????
     * @param response    ????????????
     * @param path        ????????????
     * @param bytes       ???????????????
     * @param contentType ????????????
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
                     "??????????????????????????????");
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
     * Word?????????Pdf
     *
     * @param id                Word????????????
     * @param withTransactional ??????
     * @return Pdf????????????
     */
    private String word2PdfReturnId(String id,
                                    boolean withTransactional) {
        try {
            FileInfo fileInfo = repository_Key.withTransactional(withTransactional)
                                              .getByIdAndCheckNull(id,
                                                                   FileInfo.class,
                                                                   1,
                                                                   "??????????????????????????????");

            checkFileStateThrowExceptionWhenError(fileInfo.getState());

            if ((!StringExtension.ignoreCaseEquals(fileInfo.getExtension(),
                                                   ".doc")
                    && !StringExtension.ignoreCaseEquals(fileInfo.getExtension(),
                                                         ".docx"))
                    || !fileInfo.getStorageType()
                                .equals(StorageType.????????????))
                throw new BusinessException("???????????????????????????PDF??????");

            String wordFilePath = getFilePath(fileInfo.getPath());

            String pdfPath = String.format("%s.pdf",
                                           PathExtension.trimExtension(fileInfo.getPath(),
                                                                       fileInfo.getExtension()));

            String savePath = getFilePath(pdfPath);

            //PDF?????????????????????
            String history_id = repository_Key.select()
                                              .columns(F_Fields.id)
                                              .where(x -> x.and(F_Fields.path,
                                                                FilterCompare.Eq,
                                                                pdfPath)
                                                           .and(F_Fields.storageType,
                                                                FilterCompare.Eq,
                                                                StorageType.????????????)
                                                           .and(F_Fields.state,
                                                                FilterCompare.Eq,
                                                                FileState.??????))
                                              .first(String.class);

            if (StringUtils.hasText(history_id)) {
                if (new File(savePath).exists())
                    return history_id;

                if (repository_Key.updateDiy()
                                  .set(F_Fields.state,
                                       FileState.?????????)
                                  .where(x -> x.and(F_Fields.id,
                                                    FilterCompare.Eq,
                                                    history_id))
                                  .executeAffrows() < 0)
                    throw new Exception("??????????????????????????????");
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
                                     StorageType.????????????,
                                     fileInfo.getState(),
                                     withTransactional);

            return file.getId();
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage(),
                                        ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            throw new BusinessException("??????????????????",
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
                                               FileState.?????????)
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
                                               StorageType.????????????)
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
            throw new BusinessException("????????????????????????",
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
            throw new BusinessException("??????????????????????????????",
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
                                                    FileState.??????,
                                                    FileState.?????????,
                                                    FileState.?????????))
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
            throw new BusinessException("??????????????????????????????");
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
                                               FileState.?????????)
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
            throw new BusinessException("????????????????????????",
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
                throw new BusinessException("????????????????????????");
        } catch (Exception ex) {
            throw new BusinessException("????????????????????????",
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
                        .equals(StorageType.????????????) && StringUtils.hasText(file.getPath())) {
                    //????????????
                    String path = getFilePath(file.getPath());

                    if (!FileExtension.delete(path))
                        throw new BusinessException("??????????????????");

                    String screenshot = String.format("%s-Screenshot",
                                                      PathExtension.trimExtension(path));
                    if (!FileExtension.delete(screenshot))
                        throw new BusinessException("????????????????????????");
                }

                if (repository_Key.updateDiy()
                                  .set(F_Fields.state,
                                       FileState.?????????)
                                  .where(x -> x.and(F_Fields.id,
                                                    FilterCompare.Eq,
                                                    file.getId()))
                                  .executeAffrows() < 0)
                    throw new BusinessException("????????????????????????");

                if (repository_Key_PersonalFile.withTransactional(withTransactional)
                                               .updateDiy()
                                               .set(PFI_Fields.state,
                                                    PersonalFileState.?????????)
                                               .where(x -> x.and(PFI_Fields.fileId,
                                                                 FilterCompare.Eq,
                                                                 file.getId()))
                                               .executeAffrows() < 0)
                    throw new BusinessException("??????????????????????????????");
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
                                                                   "??????????????????????????????");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.????????????:
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.????????????:
                    //???????????????????????????
                    break;
                default:
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "???????????????.jpg")
                                      .toString(),
                                 "image/jpg");
                    return;
            }

            //????????????
            String path = getFilePath(fileInfo.getPath());
            File file = new File(path);
            if (!file.exists())
                throw new NullResultException("??????????????????????????????");

            String imagePath;
            String contentType;
            switch (fileInfo.getFileType()) {
                case FileType.??????:
                    imagePath = getImageThumbnail(path,
                                                  width,
                                                  height,
                                                  null);
                    contentType = fileInfo.getContentType();
                    break;
                case FileType.??????:
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
                                                         "????????????????????????.jpg")
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
                         "??????????????????????????????");
            }
        } catch (Exception ex) {
            logger.error("????????????",
                         ex);
            try {
                responseFile(request,
                             response,
                             getImageThumbnail(Paths.get(fileStateDirectory(),
                                                         "????????????.jpg")
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
                         "????????????");
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
                                                                   "??????????????????????????????");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.????????????:
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.????????????:
                    if ("application/msword".equals(fileInfo.getContentType())
                            || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileInfo.getContentType())
                            || ".doc".equals(fileInfo.getExtension())
                            || ".docx".equals(fileInfo.getExtension())) {
                        //word?????????pdf????????????
                        AtomicReference<FileInfo> pdfFileInfo = new AtomicReference<>();
                        pdfFileInfo.set(word2Pdf(fileInfo.getId(),
                                                 true));

                        //????????????
                        responseFile(request,
                                     response,
                                     getFilePath(pdfFileInfo.get()
                                                            .getPath()),
                                     pdfFileInfo.get()
                                                .getBytes(),
                                     pdfFileInfo.get()
                                                .getContentType());
                        break;
                    } else if (Arrays.stream(new String[]{FileType.??????,
                                                          FileType.??????,
                                                          FileType.????????????})
                                     .anyMatch(x -> x.equals(fileInfo.getFileType()))
                            || "application/pdf".equals(fileInfo.getContentType())
                            || ".pdf".equals(fileInfo.getExtension())) {
                        //????????????
                        responseFile(request,
                                     response,
                                     getFilePath(fileInfo.getPath()),
                                     fileInfo.getBytes(),
                                     fileInfo.getContentType());
                        break;
                    }
                default:
                    //????????????
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "???????????????.jpg")
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
                                       "????????????????????????.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.NOT_FOUND,
                         "??????????????????????????????");
            }
        } catch (Exception ex) {
            logger.error("????????????",
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "????????????.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.INTERNAL_SERVER_ERROR,
                         "????????????");
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
                                                                   "??????????????????????????????");

            if (checkFileStateResponseIsError(fileInfo.getState()))
                return;

            switch (fileInfo.getStorageType()) {
                case StorageType.????????????:
                    //???????????????
                    response.sendRedirect(fileInfo.getPath());
                    return;
                case StorageType.????????????:
                    //????????????
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
                    //????????????
                    responseFile(request,
                                 response,
                                 Paths.get(fileStateDirectory(),
                                           "???????????????.jpg")
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
                                       "????????????????????????.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.NOT_FOUND,
                         "??????????????????????????????");
            }
        } catch (Exception ex) {
            logger.error("????????????",
                         ex);
            try {
                responseFile(request,
                             response,
                             Paths.get(fileStateDirectory(),
                                       "????????????.jpg")
                                  .toString(),
                             "image/jpg");
            } catch (Exception ex1) {
                response(response,
                         HttpStatus.INTERNAL_SERVER_ERROR,
                         "????????????");
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
                                                                   "??????????????????????????????");

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
                case StorageType.????????????:
                    //????????????
                    FileExtension.download(fileInfo.getPath(),
                                           savePath);
                    break;
                case StorageType.????????????:
                    //????????????
                    FileExtension.copy(getFilePath(fileInfo.getPath()),
                                       savePath);
                    break;
                default:
                    //????????????
                    throw new BusinessException("????????????????????????");
            }
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage(),
                                        ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
                     "????????????");
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
                                                                   "??????????????????????????????");
            checkFileStateThrowExceptionWhenError(fileInfo.getState());

            if (!fileInfo.getFileType()
                         .equals(FileType.??????))
                throw new BusinessException("?????????????????????????????????????????????");

            if (!fileInfo.getStorageType()
                         .equals(StorageType.????????????))
                throw new BusinessException("??????????????????????????????????????????");

            //??????????????????
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
            throw new BusinessException("????????????????????????",
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
                                                        FileState.??????))
                                 .toList(LibraryInfo.class);
//            repository_Key.select()
//                    .where(x -> x.and(CFFields.state, FilterCompare.Eq, FileState.??????))
//                    .groupBy(x->)
        } catch (Exception ex) {
            throw new BusinessException("???????????????????????????",
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
                throw new BusinessException("??????????????????????????????");

            checkFileStateThrowExceptionWhenError((String) RepositoryExtension.getMapValueByFieldName(info,
                                                                                                      F_Fields.state));

            return getFilePath((String) RepositoryExtension.getMapValueByFieldName(info,
                                                                                   F_Fields.path));
        } catch (Exception ex) {
            throw new BusinessException("????????????????????????",
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
