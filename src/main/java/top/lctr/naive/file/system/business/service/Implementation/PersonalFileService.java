package top.lctr.naive.file.system.business.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.extension.mybatis.edge.core.provider.standard.INaiveSql;
import project.extension.mybatis.edge.dbContext.repository.IBaseRepository_Key;
import project.extension.mybatis.edge.extention.datasearch.DataSearchDTO;
import project.extension.mybatis.edge.extention.datasearch.DataSearchExtension;
import project.extension.mybatis.edge.model.DbType;
import project.extension.mybatis.edge.model.FilterCompare;
import project.extension.mybatis.edge.model.NullResultException;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.business.service.Interface.IPersonalFileService;
import top.lctr.naive.file.system.dto.PersonalFileState;
import top.lctr.naive.file.system.dto.fileDTO.FileInfo;
import top.lctr.naive.file.system.dto.personalFileDTO.DownloadFunUse_Info;
import top.lctr.naive.file.system.dto.personalFileDTO.Edit;
import top.lctr.naive.file.system.dto.personalFileDTO.FunUse_Info;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;
import top.lctr.naive.file.system.entity.CommonPersonalFile;
import top.lctr.naive.file.system.entityFields.PFI_Fields;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Paths;
import java.util.*;

/**
 * 个人文件信息服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class PersonalFileService
        implements IPersonalFileService {
    public PersonalFileService(IFileService fileService,
                               INaiveSql naiveSql,
                               IEntityExtension entityExtension) {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes != null) {
            this.request = servletRequestAttributes.getRequest();
            this.response = servletRequestAttributes.getResponse();
        } else {
//            throw new Exception("获取ServletRequestAttributes对象失败");
            this.request = null;
            this.response = null;
        }
        this.orm = naiveSql;
        this.repository_Key = naiveSql.getRepository_Key(CommonPersonalFile.class,
                                                         String.class);
        this.tableKeyAliasMap = new HashMap<>();
        this.tableKeyAliasMap.put(defaultTableKey,
                                  "a");
        this.fileService = fileService;
        this.entityExtension = entityExtension;
    }

    private final INaiveSql orm;

    private final IBaseRepository_Key<CommonPersonalFile, String> repository_Key;

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
     * 文件服务
     */
    private final IFileService fileService;

    private final IEntityExtension entityExtension;

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
            case PersonalFileState.可用:
                return false;
            case PersonalFileState.已删除:
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "已删除.jpg")
                                              .toString(),
                                         "image/jpg");
                return true;
            case PersonalFileState.不可用:
            case PersonalFileState.锁定:
            default:
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
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
            case PersonalFileState.可用:
                return;
            case PersonalFileState.已删除:
                throw new BusinessException("文件已删除");
            case PersonalFileState.锁定:
            case PersonalFileState.不可用:
            default:
                throw new BusinessException("文件不可用");
        }
    }

    /**
     * 保存文件指指定目录
     *
     * @param state       状态
     * @param fileId      文件信息Id
     * @param saveDirPath 存储文件的文件夹路径
     * @param name        文件名
     * @param rename      下载文件重命名
     */
    private void save(String state,
                      String fileId,
                      String saveDirPath,
                      String name,
                      @Nullable
                              String rename) {
        try {
            checkFileStateThrowExceptionWhenError(state);

            fileService.save(fileId,
                             saveDirPath,
                             StringUtils.hasText(rename)
                             ? rename
                             : name);
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
    public List<PersonalFile> list(DataSearchDTO dataSearch)
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .as(tableKeyAliasMap.get(defaultTableKey))
                                 .withSql(
                                         "SELECT a.*, b.`file_type`, b.`content_type`, b.`md5`, b.`storage_type`, b.`bytes`, b.`size` \r\n"
                                                 + "FROM `common_personal_file` AS a \r\n"
                                                 + "LEFT JOIN `common_file` AS b ON b.`id` = a.`file_id` ",
                                         DbType.JdbcMySQL8,
                                         DbType.JdbcMariaDB10)
                                 .withSql(
                                         "SELECT a.*, b.[FileType], b.[ContentType], b.[Md5], b.[StorageType], b.[Bytes], b.[Size] \r\n"
                                                 + "FROM [CommonPersonalFile] AS a \r\n"
                                                 + "LEFT JOIN [CommonFile] AS b ON b.[Id] = a.[FileId] ",
                                         DbType.JdbcSqlServer,
                                         DbType.JdbcSqlServer_2012_plus)
                                 .withSql(
                                         "SELECT a.*, b.\"FILE_TYPE\", b.\"CONTENT_TYPE\", b.\"MD5\", b.\"STORAGE_TYPE\", b.\"BYTES\", b.\"SIZE\" \r\n"
                                                 + "FROM \"COMMON_PERSONAL_FILE\" AS a \r\n"
                                                 + "LEFT JOIN \"COMMON_FILE\" AS b ON b.\"ID\" = a.\"FILE_ID\" ",
                                         DbType.JdbcDameng6,
                                         DbType.JdbcDameng7,
                                         DbType.JdbcDameng8,
                                         DbType.JdbcOracle12c,
                                         DbType.JdbcOracle18c,
                                         DbType.JdbcOracle19c,
                                         DbType.JdbcOracle21c)
                                 .withSql(
                                         "SELECT a.*, b.\"FileType\", b.\"ContentType\", b.\"Md5\", b.\"StorageType\", b.\"Bytes\", b.\"Size\" \r\n"
                                                 + "FROM \"CommonPersonalFile\" AS a \r\n"
                                                 + "LEFT JOIN \"CommonFile\" AS b ON b.\"Id\" = a.\"FileId\" ",
                                         DbType.JdbcPostgreSQL15)
                                 .where(x -> x.and(DataSearchExtension.toDynamicFilter(dataSearch.getFilters(),
                                                                                       tableKeyAliasMap)))
                                 .orderBy(x -> dataSearch.getOrder() == null
                                               ? x.orderBy(
                                         PFI_Fields.createTime)
                                               : x.orderBy(DataSearchExtension.toDynamicOrder(dataSearch.getOrder(),
                                                                                              tableKeyAliasMap)))
                                 .pagination(dataSearch.getPagination())
                                 .toList(PersonalFile.class);
        } catch (Exception ex) {
            throw new BusinessException("查询数据失败",
                                        ex);
        }
    }

    @Override
    public PersonalFile detail(String id)
            throws
            BusinessException {
        try {
            PersonalFile data = repository_Key.select()
                                              .withSql(
                                                      "SELECT a.*, b.`file_type`, b.`content_type`, b.`md5`, b.`storage_type`, b.`bytes`, b.`size` FROM `common_personal_file` AS a \r\n"
                                                              + "LEFT JOIN `common_file` AS b ON b.`id` = a.`file_id` ",
                                                      DbType.JdbcMySQL8,
                                                      DbType.JdbcMariaDB10)
                                              .withSql(
                                                      "SELECT a.*, b.[FileType], b.[ContentType], b.[Md5], b.[StorageType], b.[Bytes], b.[Size] FROM [CommonPersonalFile] AS a \r\n"
                                                              + "LEFT JOIN [CommonFile] AS b ON b.[Id] = a.[FileId] ",
                                                      DbType.JdbcSqlServer,
                                                      DbType.JdbcSqlServer_2012_plus)
                                              .withSql(
                                                      "SELECT a.*, b.\"FILE_TYPE\", b.\"CONTENT_TYPE\", b.\"MD5\", b.\"STORAGE_TYPE\", b.\"BYTES\", b.\"SIZE\" FROM \"COMMON_PERSONAL_FILE\" AS a \r\n"
                                                              + "LEFT JOIN \"COMMON_FILE\" AS b ON b.\"ID\" = a.\"FILE_ID\" ",
                                                      DbType.JdbcDameng6,
                                                      DbType.JdbcDameng7,
                                                      DbType.JdbcDameng8,
                                                      DbType.JdbcOracle12c,
                                                      DbType.JdbcOracle18c,
                                                      DbType.JdbcOracle19c,
                                                      DbType.JdbcOracle21c)
                                              .withSql(
                                                      "SELECT a.*, b.\"FileType\", b.\"ContentType\", b.\"Md5\", b.\"StorageType\", b.\"Bytes\", b.\"Size\" FROM \"CommonPersonalFile\" AS a \r\n"
                                                              + "LEFT JOIN \"CommonFile\" AS b ON b.\"Id\" = a.\"FileId\" ",
                                                      DbType.JdbcPostgreSQL15)
                                              .where(x -> x.and(PFI_Fields.id,
                                                                FilterCompare.Eq,
                                                                id))
                                              .mainTagLevel(1)
                                              .first(PersonalFile.class);
            if (data == null)
                throw new NullResultException("文件不存在或已被移除");
            return data;
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage());
        } catch (Throwable ex) {
            throw new BusinessException("获取详情数据失败",
                                        ex);
        }
    }

    @Override
    public String find(String fileId,
                       String configCode,
                       String name,
                       String state,
                       String createBy)
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .where(x ->
                                                x.and(PFI_Fields.fileId,
                                                      FilterCompare.Eq,
                                                      fileId)
                                                 .and(PFI_Fields.configCode,
                                                      FilterCompare.Eq,
                                                      configCode)
                                                 .and(PFI_Fields.name,
                                                      FilterCompare.Eq,
                                                      name)
                                                 .and(PFI_Fields.state,
                                                      FilterCompare.Eq,
                                                      state)
                                                 .and(PFI_Fields.createBy,
                                                      FilterCompare.Eq,
                                                      createBy))
                                 .first(PFI_Fields.id,
                                        String.class);
        } catch (Throwable ex) {
            throw new BusinessException("查询失败",
                                        ex);
        }
    }

    @Override
    public void changeState(String fileId,
                            String just4state,
                            String state)
            throws
            BusinessException {
        try {
            repository_Key.updateDiy()
                          .set(PFI_Fields.state,
                               state)
                          .set(PFI_Fields.updateTime,
                               new Date())
                          .where(x -> x.and(PFI_Fields.fileId,
                                            FilterCompare.Eq,
                                            fileId)
                                       .and(PFI_Fields.state,
                                            FilterCompare.Eq,
                                            just4state))
                          .executeAffrows();
        } catch (Exception ex) {
            throw new BusinessException("更新状态失败",
                                        ex);
        }
    }

    @Override
    public List<PersonalFile> detailList(Collection<String> ids)
            throws
            BusinessException {
        try {
            return repository_Key.select()
                                 .as(tableKeyAliasMap.get(defaultTableKey))
                                 .withSql(
                                         "SELECT a.*, b.`file_type`, b.`content_type`, b.`md5`, b.`storage_type`, b.`bytes`, b.`size` FROM `common_personal_file` AS a \r\n"
                                                 + "LEFT JOIN `common_file` AS b ON b.`id` = a.`file_id` ",
                                         DbType.JdbcMySQL8,
                                         DbType.JdbcMariaDB10)
                                 .withSql(
                                         "SELECT a.*, b.[FileType], b.[ContentType], b.[Md5], b.[StorageType], b.[Bytes], b.[Size] FROM [CommonPersonalFile] AS a \r\n"
                                                 + "LEFT JOIN [CommonFile] AS b ON b.[Id] = a.[FileId] ",
                                         DbType.JdbcSqlServer,
                                         DbType.JdbcSqlServer_2012_plus)
                                 .withSql(
                                         "SELECT a.*, b.\"FILE_TYPE\", b.\"CONTENT_TYPE\", b.\"MD5\", b.\"STORAGE_TYPE\", b.\"BYTES\", b.\"SIZE\" FROM \"COMMON_PERSONAL_FILE\" AS a \r\n"
                                                 + "LEFT JOIN \"COMMON_FILE\" AS b ON b.\"ID\" = a.\"FILE_ID\" ",
                                         DbType.JdbcDameng6,
                                         DbType.JdbcDameng7,
                                         DbType.JdbcDameng8,
                                         DbType.JdbcOracle12c,
                                         DbType.JdbcOracle18c,
                                         DbType.JdbcOracle19c,
                                         DbType.JdbcOracle21c)
                                 .withSql(
                                         "SELECT a.*, b.\"FileType\", b.\"ContentType\", b.\"Md5\", b.\"StorageType\", b.\"Bytes\", b.\"Size\" FROM \"CommonPersonalFile\" AS a \r\n"
                                                 + "LEFT JOIN \"CommonFile\" AS b ON b.\"Id\" = a.\"FileId\" ",
                                         DbType.JdbcPostgreSQL15)
                                 .where(x -> x.and(PFI_Fields.id,
                                                   FilterCompare.InSet,
                                                   ids))
                                 .toList(PersonalFile.class);
        } catch (Exception ex) {
            throw new BusinessException("获取详情数据集合失败",
                                        ex);
        }
    }

    @Override
    public CommonPersonalFile create(String name,
                                     String extension,
                                     String fileId,
                                     String state)
            throws
            BusinessException {
        try {
            CommonPersonalFile data = new CommonPersonalFile();
            data.setName(name);
            data.setExtension(extension);
            data.setFileId(fileId);
            data.setState(state);
            repository_Key.insert(entityExtension.initialization(data));
            return data;
        } catch (Exception ex) {
            throw new BusinessException("新增个人文件信息失败",
                                        ex);
        }
    }

    @Override
    public String create(String configCode,
                         String name,
                         String extension,
                         String fileId,
                         String state)
            throws
            BusinessException {
        try {
            CommonPersonalFile data = new CommonPersonalFile();
            data.setConfigCode(configCode);
            data.setName(name);
            data.setExtension(extension);
            data.setFileId(fileId);
            data.setState(state);
            repository_Key.insert(entityExtension.initialization(data));
            return data.getId();
        } catch (Exception ex) {
            throw new BusinessException("新增个人文件信息失败",
                                        ex);
        }
    }

    @Override
    public void create(String id,
                       String configCode,
                       String name,
                       String extension,
                       String fileId,
                       String state)
            throws
            BusinessException {
        try {
            CommonPersonalFile data
                    = entityExtension.initialization(new CommonPersonalFile());
            data.setId(id);
            data.setName(name);
            data.setExtension(extension);
            data.setFileId(fileId);
            data.setState(state);
            repository_Key.insert(data);
        } catch (Exception ex) {
            throw new BusinessException("新增个人文件信息失败",
                                        ex);
        }
    }

    @Override
    public void rename(String id,
                       String fileName)
            throws
            BusinessException {
        try {
            if (repository_Key.updateDiy()
                              .set(PFI_Fields.name,
                                   fileName)
                              .where(x -> x.and(PFI_Fields.id,
                                                FilterCompare.Eq,
                                                id))
                              .executeAffrows() < 0)
                throw new Exception("数据库受影响行数有误");
        } catch (Exception ex) {
            throw new BusinessException("重命名失败",
                                        ex);
        }
    }

    @Override
    public Edit edit(String id)
            throws
            BusinessException {
        try {
            return repository_Key.getByIdAndCheckNull(id,
                                                      Edit.class,
                                                      1);
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException("获取编辑数据失败",
                                        ex);
        }
    }

    @Override
    public void edit(Edit data)
            throws
            BusinessException {
        try {
            if (!repository_Key.select()
                               .where(x ->
                                              x.and(PFI_Fields.id,
                                                    FilterCompare.NotEq,
                                                    data.getId()))
                               .any())
                throw new BusinessException("数据不存在或已被移除");

            repository_Key.update(entityExtension.modify(data),
                                  Edit.class,
                                  2);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("编辑数据失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public void delete(Collection<String> ids)
            throws
            BusinessException {
        try {
            repository_Key.deleteByIds(ids);
        } catch (Exception ex) {
            throw new BusinessException("删除数据失败",
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
        try {
            FunUse_Info info = repository_Key.select()
                                             .where(x -> x.and(PFI_Fields.id,
                                                               FilterCompare.Eq,
                                                               id))
                                             .first(FunUse_Info.class);

            if (info == null)
                throw new NullResultException("文件不存在或已被删除");

            if (checkFileStateResponseIsError(info.getState()))
                return;

            fileService.preview(info.getFileId(),
                                width,
                                height,
                                time);
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "不存在或已被删除.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                FileService.response(response,
                                     HttpStatus.NOT_FOUND,
                                     "文件不存在或已被删除");
            }
        } catch (Exception ex) {
            logger.error("预览失败",
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "处理失败.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                FileService.response(response,
                                     HttpStatus.INTERNAL_SERVER_ERROR,
                                     "预览失败");
            }
        }
    }

    @Override
    public void browse(String id)
            throws
            BusinessException {
        try {
            FunUse_Info info = repository_Key.select()
                                             .where(x -> x.and(PFI_Fields.id,
                                                               FilterCompare.Eq,
                                                               id))
                                             .first(FunUse_Info.class);

            if (info == null)
                throw new NullResultException("文件不存在或已被删除");

            if (checkFileStateResponseIsError(info.getState()))
                return;

            fileService.browse(info.getFileId());
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "不存在或已被删除.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                logger.error("预览失败",
                             ex1);
//                FileImpl.response(response,
//                                  HttpStatus.NOT_FOUND,
//                                  ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error("浏览失败",
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "处理失败.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                logger.error("预览失败",
                             ex1);
//                FileImpl.response(response,
//                                  HttpStatus.INTERNAL_SERVER_ERROR,
//                                  "预览失败");
            }
        }
    }

    @Override
    public void download(String id,
                         String rename)
            throws
            BusinessException {
        try {
            DownloadFunUse_Info info = repository_Key.select()
                                                     .where(x -> x.and(PFI_Fields.id,
                                                                       FilterCompare.Eq,
                                                                       id))
                                                     .first(DownloadFunUse_Info.class);

            if (info == null)
                throw new NullResultException("文件不存在或已被删除");

            if (checkFileStateResponseIsError(info.getState()))
                return;

            fileService.download(info.getFileId(),
                                 StringUtils.hasText(rename)
                                 ? rename
                                 : info.getName());
        } catch (NullResultException ex) {
            logger.error(ex.getMessage(),
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "不存在或已被删除.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                FileService.response(response,
                                     HttpStatus.NOT_FOUND,
                                     "文件不存在或已被删除");
            }
        } catch (Exception ex) {
            logger.error("下载失败",
                         ex);
            try {
                FileService.responseFile(request,
                                         response,
                                         Paths.get(fileService.getFileStateDirectory(),
                                                   "处理失败.jpg")
                                              .toString(),
                                         "image/jpg");
            } catch (Exception ex1) {
                FileService.response(response,
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
            DownloadFunUse_Info info = repository_Key.select()
                                                     .where(x -> x.and(PFI_Fields.id,
                                                                       FilterCompare.Eq,
                                                                       id))
                                                     .first(DownloadFunUse_Info.class);

            if (info == null)
                throw new NullResultException("文件不存在或已被删除");

            save(info.getState(),
                 info.getFileId(),
                 saveDirPath,
                 info.getName(),
                 rename);
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
    public void save(PersonalFile info,
                     String saveDirPath,
                     String rename)
            throws
            BusinessException {
        try {
            save(info.getState(),
                 info.getFileId(),
                 saveDirPath,
                 info.getName(),
                 rename);
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
    public String getFilePathById(String id) {
        try {
            Map<String, Object> info = repository_Key.select()
                                                     .columns(PFI_Fields.state,
                                                              PFI_Fields.fileId)
                                                     .where(x -> x.and(PFI_Fields.id,
                                                                       FilterCompare.Eq,
                                                                       id))
                                                     .firstMap();
            if (info == null)
                throw new BusinessException("文件不存在或已被移除");

            checkFileStateThrowExceptionWhenError((String) orm.getMapValueByFieldName(info,
                                                                                      PFI_Fields.state));

            return fileService.getFilePathById((String) orm.getMapValueByFieldName(info,
                                                                                   PFI_Fields.fileId));
        } catch (Exception ex) {
            throw new BusinessException("获取文件路径失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public PersonalFile word2Pdf(String id) {
        try {
            FunUse_Info info = repository_Key.select()
                                             .where(x -> x.and(PFI_Fields.id,
                                                               FilterCompare.Eq,
                                                               id))
                                             .first(FunUse_Info.class);

            if (info == null)
                throw new NullResultException("文件不存在或已被删除");

            checkFileStateThrowExceptionWhenError(info.getState());

            FileInfo fileInfo = fileService.word2PdfAndReturnFileInfo(info.getFileId());

            CommonPersonalFile personalFile = create(info.getName(),
                                                     fileInfo.getExtension(),
                                                     fileInfo.getId(),
                                                     info.getState());

            return detail(personalFile.getId());
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
}
