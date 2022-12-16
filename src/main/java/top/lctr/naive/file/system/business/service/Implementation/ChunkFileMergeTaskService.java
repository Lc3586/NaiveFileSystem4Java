package top.lctr.naive.file.system.business.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import project.extension.mybatis.edge.INaiveSql;
import project.extension.mybatis.edge.core.repository.IBaseRepository_Key;
import project.extension.mybatis.edge.extention.RepositoryExtension;
import project.extension.mybatis.edge.model.FilterCompare;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.business.service.Interface.IChunkFileMergeTaskService;
import top.lctr.naive.file.system.business.service.Interface.IFileService;
import top.lctr.naive.file.system.dto.CFMTState;
import top.lctr.naive.file.system.entity.CommonChunkFileMergeTask;
import top.lctr.naive.file.system.entityFields.Base_Fields;
import top.lctr.naive.file.system.entityFields.CFMT_Fields;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 分片文件信息服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class ChunkFileMergeTaskService
        implements IChunkFileMergeTaskService {
    public ChunkFileMergeTaskService(IEntityExtension entityExtension,
                                     INaiveSql naiveSql,
                                     IFileService fileService)
            throws
            Throwable {
        this.entityExtension = entityExtension;
        this.repository_Key = naiveSql.getRepository_Key(CommonChunkFileMergeTask.class,
                                                         String.class);
        this.fileService = fileService;
    }

    private final IEntityExtension entityExtension;

    private final IBaseRepository_Key<CommonChunkFileMergeTask, String> repository_Key;

    private final IFileService fileService;

    /**
     * 日志组件
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 服务器标识
     */
    @Value("${file.serverKey}")
    private String serverKey;

    @Override
    public CommonChunkFileMergeTask create(String md5,
                                           String contentType,
                                           String extension,
                                           String name,
                                           int specs,
                                           int total,
                                           String state,
                                           boolean withTransactional)
            throws
            BusinessException {
        try {
            CommonChunkFileMergeTask data = new CommonChunkFileMergeTask();
            data.setServerKey(serverKey);
            data.setMd5(md5);
            data.setContentType(contentType);
            data.setExtension(extension);
            data.setName(name);
            data.setSpecs(specs);
            data.setTotal(total);
            data.setState(state);
            data.setBytes(0L);
            data.setCurrentChunkIndex(-1);
            repository_Key.insert(entityExtension.initialization(data));
            return data;
        } catch (Exception ex) {
            throw new BusinessException("新增分片文件合并任务信息失败",
                                        ex);
        }
    }

    @Override
    public void update(CommonChunkFileMergeTask task,
                       boolean withTransactional)
            throws
            Exception {
        repository_Key.withTransactional(withTransactional)
                      .update(task);
    }

    @Override
    public CommonChunkFileMergeTask get(String id,
                                        boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CFMT_Fields.id,
                                               FilterCompare.Eq,
                                               id))
                             .first();
    }

    @Override
    public List<String> getUnfinishedIdList(boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .columns(CFMT_Fields.id)
                             .where(x -> x.and(CFMT_Fields.state,
                                               FilterCompare.NotEq,
                                               CFMTState.已完成)
                                          .and(CFMT_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .toList(String.class);
    }

    @Override
    public List<String> getUnclearedIdList(boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .columns(CFMT_Fields.id)
                             .where(x -> x.and(CFMT_Fields.state,
                                               FilterCompare.Eq,
                                               CFMTState.待清理)
                                          .and(CFMT_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .toList(String.class);
    }

    @Override
    public Boolean isUploading(String md5,
                               Integer specs,
                               boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CFMT_Fields.md5,
                                               FilterCompare.Eq,
                                               md5)
                                          .and(CFMT_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CFMT_Fields.state,
                                               FilterCompare.Eq,
                                               CFMTState.上传中)
                                          .and(CFMT_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .any();
    }

    @Override
    public Integer lastCurrentIndex(String md5,
                                    Integer specs,
                                    boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CFMT_Fields.md5,
                                               FilterCompare.Eq,
                                               md5)
                                          .and(CFMT_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CFMT_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .orderBy(x -> x.orderByDescending(Base_Fields.updateTime))
                             .first(CFMT_Fields.currentChunkIndex,
                                    Integer.class);
    }

    @Override
    public Boolean isAlreadyExist(String md5,
                                  Integer specs,
                                  Integer total,
                                  boolean withTransactional)
            throws
            Exception {
        List<java.util.Map<String, Object>> list
                = repository_Key.withTransactional(withTransactional)
                                .select()
                                .where(x -> x.and(CFMT_Fields.md5,
                                                  FilterCompare.Eq,
                                                  md5)
                                             .and(CFMT_Fields.specs,
                                                  FilterCompare.Eq,
                                                  specs)
                                             .and(CFMT_Fields.total,
                                                  FilterCompare.Eq,
                                                  total)
                                             .and(CFMT_Fields.state,
                                                  FilterCompare.NotEq,
                                                  CFMTState.失败)
                                             .and(CFMT_Fields.state,
                                                  FilterCompare.NotEq,
                                                  CFMTState.已失效)
                                             .and(CFMT_Fields.serverKey,
                                                  FilterCompare.Eq,
                                                  serverKey))
                                .columns(CFMT_Fields.id,
                                         CFMT_Fields.path,
                                         CFMT_Fields.state)
                                .toMapList();

        if (list == null || list.size() == 0)
            return false;

        List<String> deleteIds = new ArrayList<>();
        for (java.util.Map<String, Object> map : list) {
            String id = (String) RepositoryExtension.getMapValueByFieldName(map,
                                                                            CFMT_Fields.id);
            String path = String.valueOf(RepositoryExtension.getMapValueByFieldName(map,
                                                                                    CFMT_Fields.path));
            String state = (String) RepositoryExtension.getMapValueByFieldName(map,
                                                                               CFMT_Fields.state);
            if (!state.equals(CFMTState.待清理) && !state.equals(CFMTState.已完成))
                continue;

            if (!StringUtils.hasText(path)
                    || !new File(Paths.get(fileService.getWWWRootDirectory(),
                                           path)
                                      .toString()).exists())
                deleteIds.add(id);
        }

        if (deleteIds.size() > 0
                && repository_Key.updateDiy()
                                 .set(CFMT_Fields.state,
                                      CFMTState.已失效)
                                 .where(x -> x.and(CFMT_Fields.id,
                                                   FilterCompare.InSet,
                                                   deleteIds))
                                 .executeAffrows() < 0)
            throw new BusinessException("更新失效数据失败");

        return list.size() - deleteIds.size() > 0;
    }

    @Override
    public CommonChunkFileMergeTask getAlreadyTask(String md5,
                                                   Integer specs,
                                                   Integer total,
                                                   boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CFMT_Fields.md5,
                                               FilterCompare.Eq,
                                               md5)
                                          .and(CFMT_Fields.specs,
                                               FilterCompare.Eq,
                                               specs)
                                          .and(CFMT_Fields.total,
                                               FilterCompare.Eq,
                                               total)
                                          .and(CFMT_Fields.state,
                                               FilterCompare.NotEq,
                                               CFMTState.失败)
                                          .and(CFMT_Fields.state,
                                               FilterCompare.NotEq,
                                               CFMTState.已失效)
                                          .and(CFMT_Fields.serverKey,
                                               FilterCompare.Eq,
                                               serverKey))
                             .first();
    }

    @Override
    public Boolean isExpire(String id,
                            boolean withTransactional)
            throws
            Exception {
        return repository_Key.withTransactional(withTransactional)
                             .select()
                             .where(x -> x.and(CFMT_Fields.id,
                                               FilterCompare.Eq,
                                               id)
                                          .and(CFMT_Fields.state,
                                               FilterCompare.NotEq,
                                               CFMTState.等待处理))
                             .any();
    }
}
