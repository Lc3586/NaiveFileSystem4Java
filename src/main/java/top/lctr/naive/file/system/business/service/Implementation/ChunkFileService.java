package top.lctr.naive.file.system.business.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import project.extension.file.FileExtension;
import project.extension.mybatis.edge.INaiveSql;
import project.extension.mybatis.edge.core.repository.IBaseRepository_Key;
import project.extension.mybatis.edge.model.FilterCompare;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import project.extension.tuple.Tuple2;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileService;
import top.lctr.naive.file.system.dto.FileState;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_ForMerge;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_Indices;
import top.lctr.naive.file.system.entity.CommonChunkFile;
import top.lctr.naive.file.system.entityFields.CF_Fields;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 分片文件信息服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class ChunkFileService
        implements IChunkFileService {
    public ChunkFileService(IEntityExtension entityExtension,
                            INaiveSql naiveSql)
            throws
            Throwable {
        this.entityExtension = entityExtension;
        this.repository_Key = naiveSql.getRepository_Key(CommonChunkFile.class,
                                                         String.class);
    }

    private final IEntityExtension entityExtension;

    private final IBaseRepository_Key<CommonChunkFile, String> repository_Key;

    /**
     * 日志组件
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    public FunUse_FileState getFileState(String md5,
                                         Integer specs,
                                         boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CF_Fields.md5,
                                               FilterCompare.Eq,
                                               md5)
                                          .and(CF_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CF_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .orderBy(String.format("(CASE WHEN state='%s' THEN 1 ELSE 0 END) DESC, (CASE WHEN state='%s' THEN 1 ELSE 0 END) DESC",
                                                    FileState.可用,
                                                    FileState.上传中))
                             .first(FunUse_FileState.class);
    }

    @Override
    public Long chunkFileAlreadyCount(String fileMd5,
                                      Integer specs,
                                      boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CF_Fields.fileMd5,
                                               FilterCompare.Eq,
                                               fileMd5)
                                          .and(CF_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CF_Fields.state,
                                               FilterCompare.Eq,
                                               FileState.可用)
                                          .and(CF_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .count();
    }

    @Override
    public List<FunUse_ForMerge> chunkFileAlreadyList(String fileMd5,
                                                      Integer specs,
                                                      boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CF_Fields.fileMd5,
                                               FilterCompare.Eq,
                                               fileMd5)
                                          .and(CF_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CF_Fields.state,
                                               FilterCompare.Eq,
                                               FileState.可用)
                                          .and(CF_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .orderBy(x -> x.orderBy(CF_Fields.index))
                             .toList(FunUse_ForMerge.class);
    }

    @Override
    public List<FunUse_Indices> chunkFileIndicesList(String fileMd5,
                                                     Integer specs,
                                                     boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .withSql(
                                     "SELECT inner_cf.\"INDEX\", COUNT(1) AS \"COUNT\" FROM common_chunk_file AS inner_cf \n"
                                             + "\tWHERE inner_cf.\"FILE_MD5\" = @fileMd5  AND inner_cf.\"SPECS\" = @specs  AND inner_cf.\"SERVER_KEY\" = @serverKey \n"
                                             + "\tGROUP BY inner_cf.\"INDEX\" \n",
                                     Arrays.asList(new Tuple2<>("fileMd5",
                                                                fileMd5),
                                                   new Tuple2<>("specs",
                                                                specs),
                                                   new Tuple2<>("serverKey",
                                                                serverKey)))
                             .orderBy(x -> x.orderBy(CF_Fields.index))
                             .toList(FunUse_Indices.class);
    }

    @Override
    public Date lastUploadedChunkFileCreateTime(String fileMd5,
                                                Integer specs,
                                                boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CF_Fields.fileMd5,
                                               FilterCompare.Eq,
                                               fileMd5)
                                          .and(CF_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CF_Fields.state,
                                               FilterCompare.NotEq,
                                               FileState.已删除)
                                          .and(CF_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .orderBy(x -> x.orderByDescending(CF_Fields.createTime))
                             .first(CF_Fields.createTime,
                                    Date.class);
    }

    @Override
    public CommonChunkFile create(String taskKey,
                                  String file_md5,
                                  String md5,
                                  int index,
                                  int specs,
                                  String path,
                                  boolean withTransactional)
            throws
            BusinessException {
        try {
            //是否已存在相同的分片
            String id = repository_Key.withTransactional(withTransactional)
                                      .select()
                                      .columns(CF_Fields.id)
                                      .where(x ->
                                                     x.and(CF_Fields.taskKey,
                                                           FilterCompare.Eq,
                                                           taskKey)
                                                      .and(CF_Fields.md5,
                                                           FilterCompare.Eq,
                                                           md5))
                                      .first(String.class);

            if (id != null)
                return repository_Key.getById(id);

            CommonChunkFile data = new CommonChunkFile();
            data.setTaskKey(taskKey);
            data.setServerKey(serverKey);
            data.setFileMd5(file_md5);
            data.setMd5(md5);
            data.setIndex(index);
            data.setSpecs(specs);
            data.setPath(path);
            data.setState(StringUtils.hasText(path)
                          ? FileState.可用
                          : FileState.上传中);
            repository_Key.insert(entityExtension.initialization(data));
            return data;
        } catch (Exception ex) {
            throw new BusinessException("新增分片文件信息失败",
                                        ex);
        }
    }

    @Override
    public void update(String taskKey,
                       String md5,
                       Long bytes,
                       String relativePath,
                       boolean withTransactional)
            throws
            BusinessException {
        try {
            if (!repository_Key.withTransactional(withTransactional)
                               .select()
                               .where(x ->
                                              x.and(CF_Fields.taskKey,
                                                    FilterCompare.Eq,
                                                    taskKey)
                                               .and(CF_Fields.md5,
                                                    FilterCompare.Eq,
                                                    md5))
                               .any())
                throw new BusinessException("分片文件不存在或已被移除");

            String size = FileExtension.getFileSize(bytes);

            if (repository_Key.updateDiy()
                              .set(CF_Fields.bytes,
                                   bytes)
                              .set(CF_Fields.size,
                                   size)
                              .set(CF_Fields.path,
                                   relativePath)
                              .set(CF_Fields.state,
                                   FileState.可用)
                              .where(x ->
                                             x.and(CF_Fields.taskKey,
                                                   FilterCompare.Eq,
                                                   taskKey)
                                              .and(CF_Fields.md5,
                                                   FilterCompare.Eq,
                                                   md5))
                              .executeAffrows() < 0)
                throw new Exception("数据库受影响行数有误");
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("更新分片文件信息失败",
                                        ex);
        }
    }

    @Override
    public void delete(Collection<String> ids,
                       boolean withTransactional)
            throws
            BusinessException {
        try {
            List<FunUse_ForMerge> chunkFiles = repository_Key.withTransactional(withTransactional)
                                                             .select()
                                                             .where(x -> x.and(CF_Fields.id,
                                                                               FilterCompare.InSet,
                                                                               ids))
                                                             .toList(FunUse_ForMerge.class);
            clear(chunkFiles,
                  withTransactional);
        } catch (Exception ex) {
            throw new BusinessException("删除数据失败",
                                        ex);
        }
    }

    @Override
    public void clear(String file_md5,
                      int specs,
                      boolean withTransactional)
            throws
            BusinessException {
        try {
            List<FunUse_ForMerge> chunkFiles = repository_Key.withTransactional(withTransactional)
                                                             .select()
                                                             .where(x -> x.and(CF_Fields.fileMd5,
                                                                               FilterCompare.Eq,
                                                                               file_md5)
                                                                          .and(CF_Fields.specs,
                                                                               FilterCompare.Eq,
                                                                               specs)
                                                                          .and(CF_Fields.state,
                                                                               FilterCompare.Eq,
                                                                               FileState.可用))
                                                             .toList(FunUse_ForMerge.class);
            clear(chunkFiles,
                  withTransactional);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("清理分片文件失败",
                                        ex);
        }
    }

    @Override
    public void clear(Collection<FunUse_ForMerge> chunkFiles,
                      boolean withTransactional)
            throws
            BusinessException {
        try {
            for (FunUse_ForMerge chunkFile : chunkFiles) {
                if (StringUtils.hasText(chunkFile.getPath())) {
                    //绝对路径
                    String path = getFilePath(chunkFile.getPath());

                    if (!FileExtension.delete(path))
                        throw new BusinessException("删除分片文件失败");
                }

                if (repository_Key.withTransactional(withTransactional)
                                  .updateDiy()
                                  .set(CF_Fields.state,
                                       FileState.已删除)
                                  .where(x -> x.and(CF_Fields.id,
                                                    FilterCompare.Eq,
                                                    chunkFile.getId()))
                                  .executeAffrows() < 0)
                    throw new BusinessException("更新分片文件信息失败");
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("清理分片文件失败",
                                        ex);
        }
    }

    @Override
    public String getWWWRootDirectory() {
        return wwwRootDirectory;
    }

    @Override
    public String getFilePath(String path) {
        return Paths.get(getWWWRootDirectory(),
                         path)
                    .toAbsolutePath()
                    .toString();
    }
}
