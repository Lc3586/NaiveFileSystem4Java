package top.lctr.naive.file.system.business.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import project.extension.collections.CollectionsExtension;
import project.extension.file.FileDownloadInfo;
import project.extension.file.FileExtension;
import project.extension.file.PathExtension;
import project.extension.standard.authentication.IAuthenticationService;
import project.extension.standard.authentication.Operator;
import project.extension.standard.exception.BusinessException;
import project.extension.string.StringExtension;
import sun.misc.BASE64Decoder;
import top.lctr.naive.file.system.business.handler.ChunkFileMergeHandler;
import top.lctr.naive.file.system.business.handler.Word2PdfHandler;
import top.lctr.naive.file.system.business.service.Interface.*;
import top.lctr.naive.file.system.dto.FileState;
import top.lctr.naive.file.system.dto.PUCFRState;
import top.lctr.naive.file.system.dto.PersonalFileState;
import top.lctr.naive.file.system.dto.StorageType;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.Config;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadChunkFileResponse;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadFileResponse;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;
import top.lctr.naive.file.system.entity.CommonFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 文件上传服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class FileUploadService
        implements IFileUploadService {
    public FileUploadService(IFileService fileService,
                             IPersonalFileService personalFileService,
                             IFileUploadConfigService fileUploadConfigService,
                             IChunkFileService chunkFileService,
                             ChunkFileMergeHandler chunkFileMergeHandler,
                             Word2PdfHandler word2PdfHandler,
                             IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) throw new BusinessException("获取ServletRequestAttributes对象失败");
        this.request = servletRequestAttributes.getRequest();
        this.fileService = fileService;
        this.personalFileService = personalFileService;
        this.fileUploadConfigService = fileUploadConfigService;
        this.chunkFileService = chunkFileService;
        this.chunkFileMergeHandler = chunkFileMergeHandler;
        this.word2PdfHandler = word2PdfHandler;
        this.baseDirectory = fileService.getWWWRootDirectory();
    }

    /**
     * 日志组件
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 请求对象
     */
    private final HttpServletRequest request;

    /**
     * 启用大文件上传功能
     */
    @Value("${file.upload-large-file.enable}")
    private Boolean enableUploadLargeFile;

    /**
     * 存储路径根目录绝对路径
     */
    private final String baseDirectory;

    /**
     * 文件服务
     */
    private final IFileService fileService;

    /**
     * 个人文件信息服务
     */
    private final IPersonalFileService personalFileService;

    /**
     * 文件上传配置服务
     */
    private final IFileUploadConfigService fileUploadConfigService;

    /**
     * 分片文件信息服务
     */
    private final IChunkFileService chunkFileService;

    /**
     * 分片文件合并处理类
     */
    private final ChunkFileMergeHandler chunkFileMergeHandler;

    /**
     * Word文件自动转换Pdf文件处理类
     */
    private final Word2PdfHandler word2PdfHandler;

    private final IAuthenticationService authenticationService;

    /**
     * 获取MIME类型
     *
     * @param type      MIME类型
     * @param extension 文件拓展名
     * @return MIME类型
     */
    private String getType(String type,
                           String extension)
            throws
            IOException {
        //如果mime类型为空则尝试获取该信息
        if (!StringUtils.hasText(type) && StringUtils.hasText(extension))
            return Files.probeContentType(Paths.get(String.format("just4name%s",
                                                                  extension)));
        return type;
    }

    /**
     * 使用上传配置检查文件类型是否合法
     *
     * @param configCode 上传配置编码
     * @param type       MIME类型
     * @param extension  文件拓展名
     * @param length     文件字节数
     * @return 是否非法
     */
    private boolean checkTypeIsError(String configCode,
                                     String type,
                                     String extension,
                                     Long length)
            throws
            BusinessException {
        Config config = fileUploadConfigService.config(configCode,
                                                       true);

        if (config == null) throw new BusinessException("上传配置不存在或已失效");

        if (config.getLowerSingleSize() != null && config.getLowerSingleSize() > 0) {
            long lowerSingleSize = new Double(config.getLowerSingleSize() * 1024).longValue();
            if (lowerSingleSize > length)
                throw new BusinessException(String.format("文件必须大于%s",
                                                          FileExtension.getFileSize(lowerSingleSize)));
        }

        if (config.getUpperSingleSize() != null && config.getUpperSingleSize() > 0) {
            long upperSingleSize = new Double(config.getUpperSingleSize() * 1024).longValue();
            if (upperSingleSize < length)
                throw new BusinessException(String.format("文件必须小于%s",
                                                          FileExtension.getFileSize(upperSingleSize)));
        }

        if (!CollectionsExtension.anyPlus(config.getAllowedTypeList()) && !CollectionsExtension.anyPlus(
                config.getProhibitedTypeList())) return false;

        if (CollectionsExtension.anyPlus(config.getAllowedTypeList())) {
            boolean flag = false;
            for (String allowedType : config.getAllowedTypeList()) {
                if ((StringUtils.hasText(type) && Pattern.compile(allowedType.replaceAll("/\\*",
                                                                                         "//*"),
                                                                  Pattern.CASE_INSENSITIVE)
                                                         .matcher(
                                                                 type)
                                                         .find()) || (StringUtils.hasText(extension)
                        && allowedType.indexOf(
                        ".") == 0 && StringExtension.ignoreCaseEquals(allowedType,
                                                                      extension))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return true;
        }

        if (CollectionsExtension.anyPlus(config.getProhibitedTypeList()))
            for (String prohibitedType : config.getProhibitedTypeList()) {
                if ((StringUtils.hasText(type) && Pattern.compile(prohibitedType.replaceAll("/\\*",
                                                                                            "//*"),
                                                                  Pattern.CASE_INSENSITIVE)
                                                         .matcher(
                                                                 type)
                                                         .find()) || (StringUtils.hasText(extension)
                        && prohibitedType.indexOf(
                        ".") == 0 && StringExtension.ignoreCaseEquals(prohibitedType,
                                                                      extension))) return true;
            }

        return false;
    }

    /**
     * 获取文件状态信息
     *
     * @param md5   文件MD5校验值
     * @param chunk 是否为文件分片
     * @param specs 分片文件规格
     * @return 文件状态信息
     */
    private FunUse_FileState getFileState(String md5,
                                          boolean chunk,
                                          Integer specs) {
        FunUse_FileState state = chunk
                                 ? chunkFileService.getFileState(md5,
                                                                 specs)
                                 : fileService.getFileState(md5);

        if (state == null) state = new FunUse_FileState(null,
                                                        FileState.未上传,
                                                        null);
        else if (state.getState()
                      .equals(FileState.上传中)) state.setPath(null);
        else if (state.getState()
                      .equals(FileState.可用) && !new File(fileService.getFilePath(state.getPath())).exists()) {
            if (chunk)
                chunkFileService.delete(Collections.singletonList(state.getId()));
            else
                fileService.delete(Collections.singletonList(state.getId()));

            state.setState(FileState.已删除);
            state.setPath(null);
        }

        return state;
    }

    /**
     * 上传单个分片文件
     *
     * @param key         上传标识
     * @param md5         分片文件MD5值
     * @param inputStream 输入流
     * @param length      字节数
     */
    private void singleChunkFile(String key,
                                 String md5,
                                 InputStream inputStream,
                                 Long length)
            throws
            Exception {
        //在数据库中存储相对路径
        String relativePath = Paths.get("upload",
                                        new SimpleDateFormat("yyyy-MM-dd").format(
                                                new Date()),
                                        "chunkfiles",
                                        key,
                                        String.format("%s.tmp",
                                                      md5))
                                   .toString();


        String fileName = Paths.get(baseDirectory,
                                    relativePath)
                               .toString();

        FileExtension.save(inputStream,
                           fileName,
                           length);

        if (!new File(fileName).exists()) throw new BusinessException("未上传任何文件");

        if (!FileExtension.md5(fileName,
                               md5)) {
            FileExtension.delete(fileName);
            throw new BusinessException("文件已被篡改, 上传失败");
        }

        chunkFileService.update(key,
                                md5,
                                length,
                                relativePath);
    }

    /**
     * 上传单个文件
     *
     * @param configCode       上传配置编码
     * @param inputStream      输入流
     * @param length           字节数
     * @param type             文件类型
     * @param extension        文件拓展名
     * @param personalFilename 文件重命名
     */
    private PersonalFile singleFile(String configCode,
                                    InputStream inputStream,
                                    Long length,
                                    String type,
                                    String extension,
                                    String personalFilename)
            throws
            Exception {
        //如果mime类型为空则尝试获取该信息
        type = getType(type,
                       extension);

        if (checkTypeIsError(configCode,
                             type,
                             extension,
                             length)) throw new BusinessException("文件类型不合法, 禁止上传");

        String filename = UUID.randomUUID()
                              .toString();

        //在数据库中存储相对路径
        String relativePath = Paths.get("upload",
                                        new SimpleDateFormat("yyyy-MM-dd").format(
                                                new Date()),
                                        String.format("%s%s",
                                                      filename,
                                                      extension))
                                   .toString();

        //文件存储的绝对路径
        String path = Paths.get(baseDirectory,
                                relativePath)
                           .toString();

        FileExtension.save(inputStream,
                           path,
                           length);

        if (!new File(path).exists()) throw new BusinessException("未上传任何文件");

        PersonalFile personalFile
                = create(configCode,
                         FileExtension.md5(path),
                         relativePath,
                         StorageType.相对路径,
                         length,
                         type,
                         extension,
                         filename,
                         personalFilename);

        //自动转换Word文件为Pdf文件
        if ((StringExtension.ignoreCaseEquals(personalFile.getExtension(),
                                              ".doc")
                || StringExtension.ignoreCaseEquals(personalFile.getExtension(),
                                                    ".docx"))
                && personalFile.getStorageType()
                               .equals(StorageType.相对路径))
            word2PdfHandler.add(personalFile.getFileId());

        return personalFile;
    }

    /**
     * 新增文件数据
     *
     * @param configCode       上传配置编码
     * @param md5              文件MD5校验值
     * @param relativePath     文件相对路径
     * @param storageType      存储类型
     * @param length           字节数
     * @param type             文件类型
     * @param extension        文件拓展名
     * @param filename         文件名称
     * @param personalFilename 文件重命名
     */
    private PersonalFile create(String configCode,
                                String md5,
                                String relativePath,
                                String storageType,
                                Long length,
                                String type,
                                String extension,
                                String filename,
                                String personalFilename) {
        //检查文件是否已上传过，如上传过，则删除刚刚保存的文件
        PreUploadFileResponse validation = preUploadFile(configCode,
                                                         md5,
                                                         type,
                                                         extension,
                                                         length,
                                                         personalFilename,
                                                         false,
                                                         null,
                                                         null);

        if (validation.getUploaded()) {
            if (storageType.equals(StorageType.相对路径))
                FileExtension.delete(Paths.get(baseDirectory,
                                               relativePath)
                                          .toString());
            return validation.getPersonalFile();
        }

        //新增文件信息
        CommonFile file = fileService.create(md5,
                                             filename,
                                             extension,
                                             type,
                                             length,
                                             relativePath,
                                             storageType,
                                             FileState.可用);

        //新增个人文件信息
        String personalFileId = personalFileService.create(configCode,
                                                           personalFilename,
                                                           extension,
                                                           file.getId(),
                                                           PersonalFileState.可用);

        return personalFileService.detail(personalFileId);
    }

    @Override
    @Transactional
    public PreUploadFileResponse preUploadFile(String configCode,
                                               String md5,
                                               String type,
                                               String extension,
                                               Long length,
                                               String filename,
                                               Boolean section,
                                               Integer specs,
                                               Integer total)
            throws
            BusinessException {
        try {
            //如果mime类型为空则尝试获取该信息
            type = getType(type,
                           extension);

            if (checkTypeIsError(configCode,
                                 type,
                                 extension,
                                 length)) throw new BusinessException("文件类型不合法, 禁止上传");

            FunUse_FileState state = getFileState(md5,
                                                  false,
                                                  null);

            PreUploadFileResponse result = new PreUploadFileResponse();
            result.setUploaded(state.getState()
                                    .equals(FileState.处理中) || state.getState()
                                                                   .equals(FileState.可用));

            if (result.getUploaded()) {
                //文件已上传过了，获取文件信息后返回，如未创建则自动创建
                String personalFileId = null;

                try {
                    //匿名访问上传接口时无法获取用户Id
                    personalFileId = personalFileService.find(state.getId(),
                                                              configCode,
                                                              filename,
                                                              PersonalFileState.可用,
                                                              authenticationService.tryGetOperator()
                                                                                   .orElse(new Operator())
                                                                                   .getUsername());
                } catch (Exception ignore) {

                }

                if (personalFileId == null)
                    personalFileId = personalFileService.create(configCode,
                                                                filename,
                                                                extension,
                                                                state.getId(),
                                                                PersonalFileState.可用);

                result.setPersonalFile(personalFileService.detail(personalFileId));
            } else if (section) {
                //文件未上传，但是分片上传时需要提前添加分片文件合并的任务
                if (!enableUploadLargeFile) throw new BusinessException("未启用大文件上传功能");

                //新增分片文件合并任务
                chunkFileMergeHandler.add(md5,
                                          type,
                                          extension,
                                          filename,
                                          specs,
                                          total);
            }

            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public PreUploadChunkFileResponse preUploadChunkFile(String file_md5,
                                                         String md5,
                                                         Integer index,
                                                         Integer specs,
                                                         Boolean forced)
            throws
            BusinessException {
        try {
            if (!enableUploadLargeFile) throw new BusinessException("未启用大文件上传功能");

            FunUse_FileState state = getFileState(md5,
                                                  false,
                                                  null);

            PreUploadChunkFileResponse result = new PreUploadChunkFileResponse();
            result.setKey(UUID.randomUUID()
                              .toString());

            if (state.getState()
                     .equals(FileState.处理中) || state.getState()
                                                    .equals(FileState.可用))
                result.setState(PUCFRState.全部跳过);
            else {
                if (forced) result.setState(PUCFRState.允许上传);
                else {
                    state = getFileState(md5,
                                         true,
                                         specs);
                    switch (state.getState()) {
                        case FileState.可用:
                        case FileState.处理中:
                            result.setState(PUCFRState.跳过);
                            break;
                        case FileState.上传中:
                            result.setState(PUCFRState.推迟上传);
                            break;
                        default:
                            result.setState(PUCFRState.允许上传);
                            break;
                    }
                }

                if (result.getState()
                          .equals(PUCFRState.允许上传))
                    chunkFileService.create(result.getKey(),
                                            file_md5,
                                            md5,
                                            index,
                                            specs,
                                            state.getPath());
            }
            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public void singleChunkFile(String key,
                                String md5,
                                MultipartFile file)
            throws
            BusinessException {
        try {
            if (!enableUploadLargeFile) throw new BusinessException("未启用大文件上传功能");

            if (file.isEmpty()) throw new BusinessException("请勿上传空文件");

            singleChunkFile(key,
                            md5,
                            file.getInputStream(),
                            file.getSize());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public void singleChunkFileByArrayBuffer(String key,
                                             String md5)
            throws
            BusinessException {
        try {
            if (!enableUploadLargeFile) throw new BusinessException("未启用大文件上传功能");

            if (request.getContentLengthLong() == 0) throw new BusinessException("请勿上传空文件");

            singleChunkFile(key,
                            md5,
                            request.getInputStream(),
                            request.getContentLengthLong());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public PersonalFile uploadChunkFileFinished(String configCode,
                                                String file_md5,
                                                Integer specs,
                                                Integer total,
                                                String type,
                                                String extension,
                                                String filename)
            throws
            BusinessException {
        try {
            if (!enableUploadLargeFile) throw new BusinessException("未启用大文件上传功能");

            FunUse_FileState state = getFileState(file_md5,
                                                  false,
                                                  null);

            String fileId;

            if (state.getState()
                     .equals(FileState.可用) || state.getState()
                                                   .equals(FileState.处理中)) {
                String personalFileId = null;

                try {
                    //匿名访问上传接口时无法获取用户Id
                    personalFileId = personalFileService.find(state.getId(),
                                                              configCode,
                                                              filename,
                                                              PersonalFileState.可用,
                                                              authenticationService.tryGetOperator()
                                                                                   .orElse(new Operator())
                                                                                   .getUsername());
                } catch (Exception ignore) {

                }

                if (personalFileId != null) return personalFileService.detail(personalFileId);

                fileId = state.getId();
            } else {
                CommonFile file = fileService.create(file_md5,
                                                     UUID.randomUUID()
                                                         .toString(),
                                                     extension,
                                                     type,
                                                     0L,
                                                     null,
                                                     StorageType.相对路径,
                                                     FileState.处理中);

                fileId = file.getId();
            }

            //新增个人文件信息
            String personalFileId = personalFileService.create(configCode,
                                                               filename,
                                                               extension,
                                                               fileId,
                                                               PersonalFileState.可用);

            //处理分片文件合并任务
            chunkFileMergeHandler.handler(file_md5,
                                          specs,
                                          total);

            return personalFileService.detail(personalFileId);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    public PersonalFile singleFileFromBase64(String configCode,
                                             String base64,
                                             String type,
                                             String extension,
                                             String filename)
            throws
            BusinessException {
        try {
            if (!StringUtils.hasText(base64)) throw new BusinessException("请勿上传空文件");

            byte[] buffer = new BASE64Decoder().decodeBuffer(base64);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer)) {
                return singleFile(configCode,
                                  inputStream,
                                  Long.parseLong(Integer.toString(buffer.length)),
                                  type,
                                  extension,
                                  filename);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    public PersonalFile singleFileFromUrl(String url,
                                          String filename,
                                          Boolean download)
            throws
            BusinessException {
        return singleFileFromUrl(null,
                                 url,
                                 filename,
                                 download);
    }

    @Override
    public PersonalFile singleFileFromUrl(String configCode,
                                          String url,
                                          String filename,
                                          Boolean download)
            throws
            BusinessException {
        try {
            FileDownloadInfo fileDownloadInfo = FileExtension.downloadInfo(url);

            //如果mime类型为空则尝试获取该信息
            fileDownloadInfo.setContentType(getType(fileDownloadInfo.getContentType(),
                                                    fileDownloadInfo.getExtension()));

            if (configCode != null && checkTypeIsError(configCode,
                                                       fileDownloadInfo.getContentType(),
                                                       fileDownloadInfo.getExtension(),
                                                       fileDownloadInfo.getLength()))
                throw new BusinessException("文件类型不合法, 禁止上传");

            if (download) {
                //需要下载文件
                try (InputStream inputStream = fileDownloadInfo.getInputStream()) {
                    return singleFile(configCode,
                                      inputStream,
                                      fileDownloadInfo.getLength(),
                                      fileDownloadInfo.getContentType(),
                                      fileDownloadInfo.getExtension(),
                                      StringUtils.hasText(filename)
                                      ? filename
                                      : fileDownloadInfo.getName());
                }
            } else {
                //无需下载
                return create(configCode,
                              StringExtension.md5(url,
                                                  Charset.defaultCharset()),
                              url,
                              StorageType.外链地址,
                              fileDownloadInfo.getLength(),
                              fileDownloadInfo.getContentType(),
                              fileDownloadInfo.getExtension(),
                              UUID.randomUUID()
                                  .toString(),
                              StringUtils.hasText(filename)
                              ? filename
                              : fileDownloadInfo.getName());
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public PersonalFile singleFile(String configCode,
                                   String filename,
                                   MultipartFile file)
            throws
            BusinessException {
        try {
            if (file.isEmpty()) throw new BusinessException("请勿上传空文件");

            try (InputStream inputStream = file.getInputStream()) {
                return singleFile(configCode,
                                  inputStream,
                                  file.getSize(),
                                  file.getContentType(),
                                  PathExtension.getExtension(file.getOriginalFilename()),
                                  StringUtils.hasText(filename)
                                  ? filename
                                  : PathExtension.trimExtension(
                                          file.getOriginalFilename()));
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public PersonalFile singleFileByArrayBuffer(String configCode,
                                                String type,
                                                String extension,
                                                String filename)
            throws
            BusinessException {
        try {
            if (request.getContentLengthLong() == 0) throw new BusinessException("请勿上传空文件");

            try (InputStream inputStream = request.getInputStream()) {
                return singleFile(configCode,
                                  inputStream,
                                  request.getContentLengthLong(),
                                  type,
                                  extension,
                                  filename);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("处理失败",
                                        ex);
        }
    }
}
