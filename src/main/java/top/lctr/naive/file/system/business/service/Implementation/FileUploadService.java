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
 * ??????????????????
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
                             IAuthenticationService authenticationService)
            throws
            Throwable {
        this.authenticationService = authenticationService;
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) throw new Exception("??????ServletRequestAttributes????????????");
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
     * ????????????
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * ????????????
     */
    private final HttpServletRequest request;

    /**
     * ???????????????????????????
     */
    @Value("${file.upload-large-file.enable}")
    private Boolean enableUploadLargeFile;

    /**
     * ?????????????????????????????????
     */
    private final String baseDirectory;

    /**
     * ????????????
     */
    private final IFileService fileService;

    /**
     * ????????????????????????
     */
    private final IPersonalFileService personalFileService;

    /**
     * ????????????????????????
     */
    private final IFileUploadConfigService fileUploadConfigService;

    /**
     * ????????????????????????
     */
    private final IChunkFileService chunkFileService;

    /**
     * ???????????????????????????
     */
    private final ChunkFileMergeHandler chunkFileMergeHandler;

    /**
     * Word??????????????????Pdf???????????????
     */
    private final Word2PdfHandler word2PdfHandler;

    private final IAuthenticationService authenticationService;

    /**
     * ??????MIME??????
     *
     * @param type      MIME??????
     * @param extension ???????????????
     * @return MIME??????
     */
    private String getType(String type,
                           String extension)
            throws
            IOException {
        //??????mime????????????????????????????????????
        if (!StringUtils.hasText(type) && StringUtils.hasText(extension))
            return Files.probeContentType(Paths.get(String.format("just4name%s",
                                                                  extension)));
        return type;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param configCode ??????????????????
     * @param type       MIME??????
     * @param extension  ???????????????
     * @param length     ???????????????
     * @return ????????????
     */
    private boolean checkTypeIsError(String configCode,
                                     String type,
                                     String extension,
                                     Long length)
            throws
            BusinessException {
        Config config = fileUploadConfigService.config(configCode,
                                                       true);

        if (config == null) throw new BusinessException("?????????????????????????????????");

        if (config.getLowerSingleSize() != null && config.getLowerSingleSize() > 0) {
            long lowerSingleSize = new Double(config.getLowerSingleSize() * 1024).longValue();
            if (lowerSingleSize > length)
                throw new BusinessException(String.format("??????????????????%s",
                                                          FileExtension.getFileSize(lowerSingleSize)));
        }

        if (config.getUpperSingleSize() != null && config.getUpperSingleSize() > 0) {
            long upperSingleSize = new Double(config.getUpperSingleSize() * 1024).longValue();
            if (upperSingleSize < length)
                throw new BusinessException(String.format("??????????????????%s",
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
     * ????????????????????????
     *
     * @param md5   ??????MD5?????????
     * @param chunk ?????????????????????
     * @param specs ??????????????????
     * @return ??????????????????
     */
    private FunUse_FileState getFileState(String md5,
                                          boolean chunk,
                                          Integer specs)
            throws
            Exception {
        FunUse_FileState state = chunk
                                 ? chunkFileService.getFileState(md5,
                                                                 specs,
                                                                 true)
                                 : fileService.getFileState(md5,
                                                            true);

        if (state == null) state = new FunUse_FileState(null,
                                                        FileState.?????????,
                                                        null);
        else if (state.getState()
                      .equals(FileState.?????????)) state.setPath(null);
        else if (state.getState()
                      .equals(FileState.??????) && !new File(fileService.getFilePath(state.getPath())).exists()) {
            if (chunk)
                chunkFileService.delete(Collections.singletonList(state.getId()),
                                        true);
            else
                fileService.delete(Collections.singletonList(state.getId()),
                                   true);

            state.setState(FileState.?????????);
            state.setPath(null);
        }

        return state;
    }

    /**
     * ????????????????????????
     *
     * @param key         ????????????
     * @param md5         ????????????MD5???
     * @param inputStream ?????????
     * @param length      ?????????
     */
    private void singleChunkFile(String key,
                                 String md5,
                                 InputStream inputStream,
                                 Long length)
            throws
            Exception {

        //?????????????????????????????????
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

        if (!new File(fileName).exists()) throw new BusinessException("?????????????????????");

        if (!FileExtension.md5(fileName,
                               md5)) {
            FileExtension.delete(fileName);
            throw new BusinessException("??????????????????, ????????????");
        }

        chunkFileService.update(key,
                                md5,
                                length,
                                relativePath,
                                true);
    }

    /**
     * ??????????????????
     *
     * @param configCode       ??????????????????
     * @param inputStream      ?????????
     * @param length           ?????????
     * @param type             ????????????
     * @param extension        ???????????????
     * @param personalFilename ???????????????
     */
    private PersonalFile singleFile(String configCode,
                                    InputStream inputStream,
                                    Long length,
                                    String type,
                                    String extension,
                                    String personalFilename)
            throws
            Exception {
        //??????mime????????????????????????????????????
        type = getType(type,
                       extension);

        if (checkTypeIsError(configCode,
                             type,
                             extension,
                             length)) throw new BusinessException("?????????????????????, ????????????");

        String filename = UUID.randomUUID()
                              .toString();

        //?????????????????????????????????
        String relativePath = Paths.get("upload",
                                        new SimpleDateFormat("yyyy-MM-dd").format(
                                                new Date()),
                                        String.format("%s%s",
                                                      filename,
                                                      extension))
                                   .toString();

        //???????????????????????????
        String path = Paths.get(baseDirectory,
                                relativePath)
                           .toString();

        FileExtension.save(inputStream,
                           path,
                           length);

        if (!new File(path).exists()) throw new BusinessException("?????????????????????");

        PersonalFile personalFile
                = create(configCode,
                         FileExtension.md5(path),
                         relativePath,
                         StorageType.????????????,
                         length,
                         type,
                         extension,
                         filename,
                         personalFilename);

        //????????????Word?????????Pdf??????
        if ((StringExtension.ignoreCaseEquals(personalFile.getExtension(),
                                              ".doc")
                || StringExtension.ignoreCaseEquals(personalFile.getExtension(),
                                                    ".docx"))
                && personalFile.getStorageType()
                               .equals(StorageType.????????????))
            word2PdfHandler.add(personalFile.getFileId());

        return personalFile;
    }

    /**
     * ??????????????????
     *
     * @param configCode       ??????????????????
     * @param md5              ??????MD5?????????
     * @param relativePath     ??????????????????
     * @param storageType      ????????????
     * @param length           ?????????
     * @param type             ????????????
     * @param extension        ???????????????
     * @param filename         ????????????
     * @param personalFilename ???????????????
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
        //??????????????????????????????????????????????????????????????????????????????
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
            if (storageType.equals(StorageType.????????????))
                FileExtension.delete(Paths.get(baseDirectory,
                                               relativePath)
                                          .toString());
            return validation.getPersonalFile();
        }

        //??????????????????
        CommonFile file = fileService.create(md5,
                                             filename,
                                             extension,
                                             type,
                                             length,
                                             relativePath,
                                             storageType,
                                             FileState.??????,
                                             true);

        //????????????????????????
        String personalFileId = personalFileService.create(configCode,
                                                           personalFilename,
                                                           extension,
                                                           file.getId(),
                                                           PersonalFileState.??????,
                                                           true);

        return personalFileService.detail(personalFileId,
                                          true);
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
            //??????mime????????????????????????????????????
            type = getType(type,
                           extension);

            if (checkTypeIsError(configCode,
                                 type,
                                 extension,
                                 length)) throw new BusinessException("?????????????????????, ????????????");

            FunUse_FileState state = getFileState(md5,
                                                  false,
                                                  null);

            PreUploadFileResponse result = new PreUploadFileResponse();
            result.setUploaded(state.getState()
                                    .equals(FileState.?????????) || state.getState()
                                                                   .equals(FileState.??????));

            if (result.getUploaded()) {
                //?????????????????????????????????????????????????????????????????????????????????
                String personalFileId = null;

                try {
                    //?????????????????????????????????????????????Id
                    personalFileId = personalFileService.find(state.getId(),
                                                              configCode,
                                                              filename,
                                                              PersonalFileState.??????,
                                                              authenticationService.tryGetOperator()
                                                                                   .orElse(new Operator())
                                                                                   .getUsername(),
                                                              true);
                } catch (Exception ignore) {

                }

                if (personalFileId == null)
                    personalFileId = personalFileService.create(configCode,
                                                                filename,
                                                                extension,
                                                                state.getId(),
                                                                PersonalFileState.??????,
                                                                true);

                result.setPersonalFile(personalFileService.detail(personalFileId,
                                                                  true));
            } else if (section) {
                //????????????????????????????????????????????????????????????????????????????????????
                if (!enableUploadLargeFile) throw new BusinessException("??????????????????????????????");

                //??????????????????????????????
                chunkFileMergeHandler.add(md5,
                                          type,
                                          extension,
                                          filename,
                                          specs,
                                          total,
                                          true);
            }

            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            if (!enableUploadLargeFile) throw new BusinessException("??????????????????????????????");

            FunUse_FileState state = getFileState(md5,
                                                  false,
                                                  null);

            PreUploadChunkFileResponse result = new PreUploadChunkFileResponse();
            result.setKey(UUID.randomUUID()
                              .toString());

            if (state.getState()
                     .equals(FileState.?????????) || state.getState()
                                                    .equals(FileState.??????))
                result.setState(PUCFRState.????????????);
            else {
                if (forced) result.setState(PUCFRState.????????????);
                else {
                    state = getFileState(md5,
                                         true,
                                         specs);
                    switch (state.getState()) {
                        case FileState.??????:
                        case FileState.?????????:
                            result.setState(PUCFRState.??????);
                            break;
                        case FileState.?????????:
                            result.setState(PUCFRState.????????????);
                            break;
                        default:
                            result.setState(PUCFRState.????????????);
                            break;
                    }
                }

                if (result.getState()
                          .equals(PUCFRState.????????????))
                    chunkFileService.create(result.getKey(),
                                            file_md5,
                                            md5,
                                            index,
                                            specs,
                                            state.getPath(),
                                            true);
            }
            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            if (!enableUploadLargeFile) throw new BusinessException("??????????????????????????????");

            if (file.isEmpty()) throw new BusinessException("?????????????????????");

            singleChunkFile(key,
                            md5,
                            file.getInputStream(),
                            file.getSize());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            if (!enableUploadLargeFile) throw new BusinessException("??????????????????????????????");

            if (request.getContentLengthLong() == 0) throw new BusinessException("?????????????????????");

            singleChunkFile(key,
                            md5,
                            request.getInputStream(),
                            request.getContentLengthLong());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            if (!enableUploadLargeFile) throw new BusinessException("??????????????????????????????");

            FunUse_FileState state = getFileState(file_md5,
                                                  false,
                                                  null);

            String fileId;

            if (state.getState()
                     .equals(FileState.??????) || state.getState()
                                                   .equals(FileState.?????????)) {
                String personalFileId = null;

                try {
                    //?????????????????????????????????????????????Id
                    personalFileId = personalFileService.find(state.getId(),
                                                              configCode,
                                                              filename,
                                                              PersonalFileState.??????,
                                                              authenticationService.tryGetOperator()
                                                                                   .orElse(new Operator())
                                                                                   .getUsername(),
                                                              true);
                } catch (Exception ignore) {

                }

                if (personalFileId != null) return personalFileService.detail(personalFileId,
                                                                              true);

                fileId = state.getId();
            } else {
                CommonFile file = fileService.create(file_md5,
                                                     UUID.randomUUID()
                                                         .toString(),
                                                     extension,
                                                     type,
                                                     0L,
                                                     null,
                                                     StorageType.????????????,
                                                     FileState.?????????,
                                                     true);

                fileId = file.getId();
            }

            //????????????????????????
            String personalFileId = personalFileService.create(configCode,
                                                               filename,
                                                               extension,
                                                               fileId,
                                                               PersonalFileState.??????,
                                                               true);

            //??????????????????????????????
            chunkFileMergeHandler.handler(file_md5,
                                          specs,
                                          total,
                                          true);

            return personalFileService.detail(personalFileId,
                                              true);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("????????????",
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
            if (!StringUtils.hasText(base64)) throw new BusinessException("?????????????????????");

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
            throw new BusinessException("????????????",
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

            //??????mime????????????????????????????????????
            fileDownloadInfo.setContentType(getType(fileDownloadInfo.getContentType(),
                                                    fileDownloadInfo.getExtension()));

            if (configCode != null && checkTypeIsError(configCode,
                                                       fileDownloadInfo.getContentType(),
                                                       fileDownloadInfo.getExtension(),
                                                       fileDownloadInfo.getLength()))
                throw new BusinessException("?????????????????????, ????????????");

            if (download) {
                //??????????????????
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
                //????????????
                return create(configCode,
                              StringExtension.md5(url,
                                                  Charset.defaultCharset()),
                              url,
                              StorageType.????????????,
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
            throw new BusinessException("????????????",
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
            if (file.isEmpty()) throw new BusinessException("?????????????????????");

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
            throw new BusinessException("????????????",
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
            if (request.getContentLengthLong() == 0) throw new BusinessException("?????????????????????");

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
            throw new BusinessException("????????????",
                                        ex);
        }
    }
}
