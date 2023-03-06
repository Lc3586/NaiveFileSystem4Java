package top.lctr.naive.file.system.business.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import project.extension.collections.CollectionsExtension;
import project.extension.mybatis.edge.core.provider.standard.INaiveSql;
import project.extension.mybatis.edge.core.provider.standard.curd.ISelect;
import project.extension.mybatis.edge.dbContext.repository.IBaseRepository_Key;
import project.extension.mybatis.edge.extention.datasearch.DataSearchExtension;
import project.extension.mybatis.edge.extention.datasearch.TreeDataSearchDTO;
import project.extension.mybatis.edge.model.DbType;
import project.extension.mybatis.edge.model.FilterCompare;
import project.extension.mybatis.edge.model.NullResultException;
import project.extension.mybatis.edge.model.OperationSymbol;
import project.extension.standard.api.request.datasort.DataSortDTO;
import project.extension.standard.api.request.datasort.SortMethod;
import project.extension.standard.api.request.datasort.TreeDragSortDTO;
import project.extension.standard.entity.IEntityExtension;
import project.extension.standard.exception.BusinessException;
import project.extension.string.StringExtension;
import top.lctr.naive.file.system.business.service.Interface.IFileUploadConfigService;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.*;
import top.lctr.naive.file.system.entity.CommonFileUploadConfig;
import top.lctr.naive.file.system.entityFields.FUC_Fields;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传配置服务
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Service
@Scope("prototype")
public class FileUploadConfigService
        implements IFileUploadConfigService {
    public FileUploadConfigService(IEntityExtension entityExtension,
                                   INaiveSql naiveSql) {
        this.entityExtension = entityExtension;
        this.repository_Key = naiveSql.getRepository_Key(CommonFileUploadConfig.class,
                                                         String.class);
        this.tableKeyAliasMap = new HashMap<>();
        this.tableKeyAliasMap.put(defaultTableKey,
                                  "a");
    }

    private final IEntityExtension entityExtension;

    private final IBaseRepository_Key<CommonFileUploadConfig, String> repository_Key;

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
     * 递归获取树状列表
     *
     * @param dataSearch 搜索参数
     * @param deep       处于递归中
     * @param treeIds    树状集合的id链，防止死循环
     * @return 树状列表
     */
    private List<TreeList> getTreeList(TreeDataSearchDTO dataSearch,
                                       boolean deep,
                                       List<String> treeIds) {
        if (dataSearch.getParentId() != null && !StringUtils.hasText(dataSearch.getParentId()))
            dataSearch.setParentId(null);

        List<TreeList> treeList = repository_Key.select()
                                                .as(tableKeyAliasMap.get(defaultTableKey))
                                                .where(x -> x.and(FUC_Fields.parentId,
                                                                  FilterCompare.Eq,
                                                                  dataSearch.getParentId()))
                                                .where(x -> x.and(DataSearchExtension.toDynamicFilter(dataSearch.getFilters(),
                                                                                                      tableKeyAliasMap)))
                                                .orderBy(x -> dataSearch.getOrder() == null
                                                              ? x.orderBy(FUC_Fields.level)
                                                                 .thenOrderBy(FUC_Fields.sort)
                                                              : x.orderBy(
                                                                      DataSearchExtension.toDynamicOrder(dataSearch.getOrder(),
                                                                                                         tableKeyAliasMap)))
                                                .pagination(dataSearch.getPagination())
                                                .toList(TreeList.class);

        if (CollectionsExtension.anyPlus(treeList)) {
            for (TreeList item : treeList) {
                List<String> _treeIds = deep
                                        ? CollectionsExtension.deepCopy(treeIds)
                                        : new ArrayList<>();

                dataSearch.setParentId(item.getId());

                Integer rank = dataSearch.getRank();
                if (dataSearch.getAllLevel() || rank > 1) {
                    //判断是否陷入死循环
                    if (_treeIds.contains(item.getId()))
                        return treeList;

                    _treeIds.add(item.getId());

                    if (!dataSearch.getAllLevel())
                        dataSearch.setRank(--rank);
                    item.setChildren(getTreeList(dataSearch,
                                                 true,
                                                 _treeIds));
                    item.setHasChildren(item.getChildren()
                                            .size() > 0);
                    item.setChildrenCount(item.getChildren()
                                              .size());
                } else {
                    ISelect<CommonFileUploadConfig> childrenSelect = repository_Key.select()
                                                                                   .as(tableKeyAliasMap.get(defaultTableKey))
                                                                                   .where(x -> x.and(FUC_Fields.parentId,
                                                                                                     FilterCompare.Eq,
                                                                                                     dataSearch.getParentId()))
                                                                                   .where(x -> x.and(
                                                                                           DataSearchExtension.toDynamicFilter(dataSearch.getFilters(),
                                                                                                                               tableKeyAliasMap)));
                    item.setHasChildren(childrenSelect.any());
                    item.setChildrenCount(Integer.parseInt(Long.toString(childrenSelect.count())));
                }
            }
        } else if (deep)
            treeList = new ArrayList<>();
        return treeList;
    }

    /**
     * 检测循环引用
     *
     * @param id          配置Id
     * @param referenceId 引用的配置Id
     * @return true: 存在循环引用
     */
    private boolean checkCircularReference(String id,
                                           String referenceId) {
        if (id.equals(referenceId))
            return true;

        return repository_Key.select()
                             //TODO 递归查询sql语句待完成
                             .withSql(
                                     String.format("",
                                                   referenceId,
                                                   //防止无限递归
                                                   1000),
                                     DbType.JdbcMySQL8,
                                     DbType.JdbcMariaDB10)
                             .withSql(
                                     String.format("WITH [as_tree_cte]\n"
                                                           + "as\n"
                                                           + "(\n"
                                                           + "SELECT 1 AS [cte_level], [a].[Id], [a[.[ReferenceId], [a].[ReferenceTree]\n"
                                                           + "FROM [dbo].[CommonFileUploadConfig] [a]\n"
                                                           + "WHERE([a].[Id] = '%s')\n"
                                                           + "\n"
                                                           + "UNION ALL\n"
                                                           + "\n"
                                                           + "SELECT [wct1].[cte_level] + 1 AS [cte_level], [wct2].[Id], [wct2].[ReferenceId], [wct2].[ReferenceTree]\n"
                                                           + "FROM [as_tree_cte] [wct1]\n"
                                                           + "INNER JOIN [dbo].[CommonFileUploadConfig] [wct2] ON [wct2].[Id] = [wct1].[ReferenceId]\n"
                                                           + "WHERE [wct1].[ReferenceTree] = 1 AND [wct1].[cte_level] < %s\n"
                                                           + ")\n"
                                                           + "SELECT COUNT(1)  FROM\n"
                                                           + "(\n"
                                                           + "SELECT [a].[Id], COUNT(1) [SUM]\n"
                                                           + "FROM [as_tree_cte] [a]\n"
                                                           + "GROUP BY [a].[Id]\n"
                                                           + ") [b]\n"
                                                           + "WHERE [b].[SUM] > 1",
                                                   referenceId,
                                                   //防止无限递归
                                                   100),
                                     DbType.JdbcSqlServer,
                                     DbType.JdbcSqlServer_2012_plus)
                             .withSql(
                                     String.format("SELECT COUNT(1) FROM(\n"
                                                           + "WITH \"as_tree_cte\"(cte_level, \"ID\", \"REFERENCE_ID\", \"REFERENCE_TREE\")\n"
                                                           + "as\n"
                                                           + "(\n"
                                                           + "SELECT 0 as cte_level, a.\"ID\", a.\"REFERENCE_ID\", a.\"REFERENCE_TREE\" \n"
                                                           + "FROM \"COMMON_FILE_UPLOAD_CONFIG\" a \n"
                                                           + "WHERE (a.\"ID\" = '%s')\n"
                                                           + "\n"
                                                           + "union all\n"
                                                           + "\n"
                                                           + "SELECT wct1.cte_level + 1 as cte_level, wct2.\"ID\", wct2.\"REFERENCE_ID\" , wct2.\"REFERENCE_TREE\"  \n"
                                                           + "FROM \"as_tree_cte\" wct1 \n"
                                                           + "INNER JOIN \"COMMON_FILE_UPLOAD_CONFIG\" wct2 ON wct2.\"ID\" = wct1.\"REFERENCE_ID\"\n"
                                                           + "WHERE wct1.\"REFERENCE_TREE\" = 1 AND wct1.cte_level < %s\n"
                                                           + ")\n"
                                                           + "SELECT a.\"ID\", COUNT(1) as \"TOTAL\"\n"
                                                           + "FROM \"as_tree_cte\" a \n"
                                                           + "GROUP BY a.\"ID\"\n"
                                                           + ") ",
                                                   referenceId,
                                                   //防止无限递归
                                                   100),
                                     DbType.JdbcDameng6,
                                     DbType.JdbcDameng7,
                                     DbType.JdbcDameng8,
                                     DbType.JdbcOracle12c,
                                     DbType.JdbcOracle18c,
                                     DbType.JdbcOracle19c,
                                     DbType.JdbcOracle21c)
                             //TODO 递归查询sql语句待完成
                             .withSql(
                                     String.format("",
                                                   referenceId,
                                                   //防止无限递归
                                                   100),
                                     DbType.JdbcPostgreSQL15)
                             .any();
    }

    /**
     * 获取引用配置
     *
     * @param config 配置
     */
    private void getReferenceConfig(Config config) {
        List<GetReferenceConfigFunUse_Types> referenceConfigs
                = repository_Key.select()
                                .withSql(
                                        String.format("SELECT\n"
                                                              + "\ta.`Id`,\n"
                                                              + "\ta.`Level`,\n"
                                                              + "\ta.`allowed_types`,\n"
                                                              + "\ta.`prohibited_types`\n"
                                                              + "FROM\n" +
                                                              "\t(\n"
                                                              + "\tSELECT\n"
                                                              + "\t\tcte_tbc.cte_level,\n"
                                                              + "\t\ta.`Id`,\n"
                                                              + "\t\ta.`Level`,\n"
                                                              + "\t\ta.`allowed_types`,\n"
                                                              + "\t\ta.`prohibited_types`\n"
                                                              + "\tFROM\n"
                                                              +
                                                              "\t\t(\n"
                                                              + "\t\tSELECT\n"
                                                              + "\t\t\t@cte_referenceIds AS cte_referenceIds,\n"
                                                              + "\t\t\t( SELECT @cte_referenceIds := group_concat( `reference_id` ) FROM `common_file_upload_config` WHERE find_in_set( `Id`, @cte_referenceIds ) AND @cte_treeReference = 1 ) AS cte_cReferenceIds,\n"
                                                              + "\t\t\t@cte_level := @cte_idcte_levels + 1 AS cte_level \n"
                                                              + "\t\tFROM\n"
                                                              + "\t\t\t`common_file_upload_config`,\n"
                                                              + "\t\t\t(\n"
                                                              + "\t\t\tSELECT\n"
                                                              + "\t\t\t\t@cte_referenceIds := a.`reference_id`,\n"
                                                              + "\t\t\t\t@cte_idcte_levels := 0 \n"
                                                              + "\t\t\tFROM\n"
                                                              + "\t\t\t\t`common_file_upload_config` a \n"
                                                              + "\t\t\tWHERE\n"
                                                              + "\t\t\t\t a.`Id` = '%s' \n"
                                                              + "\t\t\t\tLIMIT 1 \n"
                                                              + "\t\t\t) cte_tbb \n"
                                                              + "\t\tWHERE\n"
                                                              + "\t\t\t@cte_referenceIds IS NOT NULL \n"
                                                              + "\t\t) cte_tbc,\n"
                                                              + "\t\t`common_file_upload_config` a \n"
                                                              + "\tWHERE\n"
                                                              + "\t\tfind_in_set( a.`Id`, cte_tbc.cte_referenceIds ) \n"
                                                              + "\t\tAND cte_tbc.cte_level < %s \n"
                                                              + "\t\tAND (a.`allowed_types` IS NOT NULL OR a.`prohibited_types` IS NOT NULL) \n"
                                                              + "\t) a \n"
                                                              + "ORDER BY\n"
                                                              + "\ta.cte_level DESC,\n"
                                                              + "\ta.`Level`",
                                                      config.getId(),
                                                      //防止无限递归
                                                      100),
                                        DbType.JdbcMySQL8,
                                        DbType.JdbcMariaDB10)
                                //TODO 递归查询sql语句待完成
                                .withSql(
                                        String.format("WITH [as_tree_cte]\n"
                                                              + "as\n"
                                                              + "(\n"
                                                              + "SELECT 1 AS [cte_level], [a].[Id], [a[.[ReferenceId], [a].[ReferenceTree]\n"
                                                              + "FROM [dbo].[CommonFileUploadConfig] [a]\n"
                                                              + "WHERE([a].[Id] = '%s')\n"
                                                              + "\n"
                                                              + "UNION ALL\n"
                                                              + "\n"
                                                              + "SELECT [wct1].[cte_level] + 1 AS [cte_level], [wct2].[Id], [wct2].[ReferenceId], [wct2].[ReferenceTree]\n"
                                                              + "FROM [as_tree_cte] [wct1]\n"
                                                              + "INNER JOIN [dbo].[CommonFileUploadConfig] [wct2] ON [wct2].[Id] = [wct1].[ReferenceId]\n"
                                                              + "WHERE [wct1].[ReferenceTree] = 1 AND [wct1].[cte_level] < %s\n"
                                                              + ")\n"
                                                              + "SELECT COUNT(1)  FROM\n"
                                                              + "(\n"
                                                              + "SELECT [a].[Id], COUNT(1) [SUM]\n"
                                                              + "FROM [as_tree_cte] [a]\n"
                                                              + "ORDER BY a.cte_level ",
                                                      config.getId(),
                                                      //防止无限递归
                                                      100),
                                        DbType.JdbcSqlServer,
                                        DbType.JdbcSqlServer_2012_plus)
                                .withSql(
                                        String.format(
                                                "WITH \"as_tree_cte\"(cte_level, \"ID\", \"ALLOWED_TYPES\", \"PROHIBITED_TYPES\", \"REFERENCE_ID\" , \"REFERENCE_TREE\")\n"
                                                        + "as\n"
                                                        + "(\n"
                                                        + "SELECT 0 as cte_level, a.\"ID\", a.\"ALLOWED_TYPES\", a.\"PROHIBITED_TYPES\", a.\"REFERENCE_ID\" , a.\"REFERENCE_TREE\"\n"
                                                        + "FROM \"COMMON_FILE_UPLOAD_CONFIG\" a \n"
                                                        + "WHERE (a.\"ID\" = '%s' AND a.\"ENABLE\" = 1)\n"
                                                        + "\n"
                                                        + "union all\n"
                                                        + "\n"
                                                        + "SELECT wct1.cte_level + 1 as cte_level, wct2.\"ID\", wct2.\"ALLOWED_TYPES\", wct2.\"PROHIBITED_TYPES\", wct2.\"REFERENCE_ID\" , wct2.\"REFERENCE_TREE\"  \n"
                                                        + "FROM \"as_tree_cte\" wct1 \n"
                                                        + "INNER JOIN \"COMMON_FILE_UPLOAD_CONFIG\" wct2 ON wct2.\"ID\" = wct1.\"REFERENCE_ID\"\n"
                                                        + "WHERE wct1.\"REFERENCE_TREE\" = 1 AND wct1.cte_level < %s\n"
                                                        + ")\n"
                                                        + "SELECT * \n"
                                                        + "FROM \"as_tree_cte\" a \n"
                                                        + "ORDER BY a.cte_level",
                                                config.getId(),
                                                //防止无限递归
                                                100),
                                        DbType.JdbcDameng6,
                                        DbType.JdbcDameng7,
                                        DbType.JdbcDameng8,
                                        DbType.JdbcOracle12c,
                                        DbType.JdbcOracle18c,
                                        DbType.JdbcOracle19c,
                                        DbType.JdbcOracle21c)
                                //TODO 递归查询sql语句待完成
                                .withSql(
                                        String.format("",
                                                      config.getId(),
                                                      //防止无限递归
                                                      100),
                                        DbType.JdbcPostgreSQL15)
                                .toList(GetReferenceConfigFunUse_Types.class);

        if (CollectionsExtension.anyPlus(referenceConfigs))
            referenceConfigs.forEach(x -> unionConfigTypes(config,
                                                           x));
    }

    /**
     * 和并配置
     *
     * @param config          配置
     * @param referenceConfig 引用配置
     */
    private void unionConfigTypes(Config config,
                                  GetReferenceConfigFunUse_Types referenceConfig) {
        if (referenceConfig == null)
            return;

        if (CollectionsExtension.anyPlus(referenceConfig.getAllowedTypeList()))
            config.setAllowedTypeList(
                    CollectionsExtension.union(config.getAllowedTypeList(),
                                               referenceConfig.getAllowedTypeList()));
        if (CollectionsExtension.anyPlus(referenceConfig.getProhibitedTypeList()))
            config.setProhibitedTypeList(CollectionsExtension.union(config.getProhibitedTypeList(),
                                                                    referenceConfig.getProhibitedTypeList()));
    }

    @Override
    public List<TreeList> treeList(TreeDataSearchDTO dataSearch)
            throws
            BusinessException {
        try {
            return getTreeList(dataSearch,
                               false,
                               null);
        } catch (Exception ex) {
            throw new BusinessException("查询数据失败",
                                        ex);
        }
    }

    @Override
    public Detail detail(String id)
            throws
            BusinessException {
        try {
            return repository_Key.getByIdAndCheckNull(id,
                                                      Detail.class,
                                                      1);
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage());
        } catch (Throwable ex) {
            throw new BusinessException("获取详情数据失败",
                                        ex);
        }
    }

    @Override
    public Config config(String code,
                         boolean withTransactional)
            throws
            BusinessException {
        try {
            if (StringExtension.ignoreCaseEquals("default",
                                                 code))
                code = repository_Key.select()
                                     .where(x ->
                                                    x.and(FUC_Fields.enable,
                                                          FilterCompare.Eq,
                                                          true)
                                                     .and(FUC_Fields.public_,
                                                          FilterCompare.Eq,
                                                          true))
                                     .orderBy(x -> x.orderBy(FUC_Fields.level))
                                     .first(FUC_Fields.code,
                                            String.class);

            String finalCode = code;

            Config config = repository_Key.select()
                                          .where(x -> x.and(FUC_Fields.code,
                                                            FilterCompare.Eq,
                                                            finalCode))
                                          .mainTagLevel(1)
                                          .first(Config.class);

            if (config == null)
                throw new BusinessException("文件上传配置不存在或已被移除");

            if (!config.getEnable())
                throw new BusinessException("文件上传配置不可用");

            getReferenceConfig(config);

            return config;
        } catch (NullResultException ex) {
            throw new BusinessException(ex.getMessage());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BusinessException("获取配置数据失败",
                                        ex);
        }
    }

    @Override
    public List<Config> configList(Collection<String> codes)
            throws
            BusinessException {
        return codes.stream()
                    .map(x -> config(x,
                                     false))
                    .collect(Collectors.toList());
    }

    @Override
    public void create(Create data)
            throws
            BusinessException {
        try {
            if (data.getCode()
                    .equals("default"))
                throw new BusinessException("default为系统保留编码，请使用其他编码");

            if (repository_Key.select()
                              .where(x ->
                                             x.and(FUC_Fields.parentId,
                                                   FilterCompare.Eq,
                                                   data.getParentId())
                                              .and(FUC_Fields.code,
                                                   FilterCompare.Eq,
                                                   data.getCode()))
                              .any())
                throw new BusinessException(String.format("同层级下已存在编码为[%s]的文件上传配置",
                                                          data.getCode()));

            if (StringUtils.hasText(data.getParentId())) {
                CreateFunUse_Parent parent = repository_Key.select()
                                                           .where(x -> x.and(FUC_Fields.id,
                                                                             FilterCompare.Eq,
                                                                             data.getParentId()))
                                                           .first(CreateFunUse_Parent.class);

                if (parent == null)
                    throw new BusinessException("指定的父级不存在或已被移除");

                data.setRootId(parent.getRootId());
                data.setLevel(parent.getLevel());
            } else {
                data.setParentId(null);
                data.setRootId(null);
                data.setLevel(1);
            }

            long sort = repository_Key.select()
                                      .where(x -> x.and(FUC_Fields.parentId,
                                                        FilterCompare.Eq,
                                                        data.getParentId()))
                                      .count() + 1;

            data.setSort(Integer.parseInt(Long.toString(sort)));

            repository_Key.insert(entityExtension.initialization(data),
                                  Create.class,
                                  1);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("新增数据失败",
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
                                                      2);
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
            if (data.getCode()
                    .equals("default"))
                throw new BusinessException("default为系统保留编码，请使用其他编码");

            if (repository_Key.select()
                              .where(x ->
                                             x.and(FUC_Fields.parentId,
                                                   FilterCompare.Eq,
                                                   data.getParentId())
                                              .and(FUC_Fields.code,
                                                   FilterCompare.Eq,
                                                   data.getCode())
                                              .and(FUC_Fields.id,
                                                   FilterCompare.NotEq,
                                                   data.getId()))
                              .any())
                throw new BusinessException(String.format("同层级下已存在编码为[%s]的文件上传配置",
                                                          data.getCode()));

            if (StringUtils.hasText(data.getReferenceId()) && checkCircularReference(data.getId(),
                                                                                     data.getReferenceId()))
                throw new BusinessException("检测到循环引用");

//            java.util.List<String> sql = repository_Key.updateDiy()
//                                                       .setSource(data)
//                                                       .asDto(Edit.class)
//                                                       .mainTagLevel(1)
//                                                       .toSqlWithNoParameter();

            repository_Key.update(entityExtension.modify(data),
                                  Edit.class,
                                  1);
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
    @Transactional
    public void sort(DataSortDTO<String> data)
            throws
            BusinessException {
        try {
            //跨度为0并且也不是置顶和置底操作，判定为无意义的操作
            if (data.getSpan() == 0 && (data.getMethod() != SortMethod.TOP || data.getMethod() != SortMethod.LOW))
                return;

            Map<String, Object> current = repository_Key.select()
                                                        .columns(FUC_Fields.id,
                                                                 FUC_Fields.parentId,
                                                                 FUC_Fields.sort)
                                                        .where(x -> x.and(FUC_Fields.id,
                                                                          FilterCompare.Eq,
                                                                          data.getId()))
                                                        .firstMap();

            if (current == null || current.size() == 0)
                throw new BusinessException("数据不存在或已被移除");

            ISelect<CommonFileUploadConfig> targetSelect = repository_Key.select()
                                                                         .columns(FUC_Fields.id,
                                                                                  FUC_Fields.sort);
            Map<String, Object> target;

            switch (data.getMethod()) {
                case TOP:
                    target = targetSelect.where(
                                                 x -> x.and(FUC_Fields.parentId,
                                                            FilterCompare.Eq,
                                                            current.get(FUC_Fields.parentId)))
                                         .orderBy(x -> x.orderBy(FUC_Fields.sort))
                                         .firstMap();
                    break;
                case UP:
                    target = targetSelect.where(x ->
                                                        x.and(FUC_Fields.parentId,
                                                              FilterCompare.Eq,
                                                              current.get(FUC_Fields.parentId))
                                                         .and(FUC_Fields.sort,
                                                              FilterCompare.Lt,
                                                              current.get(FUC_Fields.sort)))
                                         .orderBy(x -> x.orderByDescending(FUC_Fields.sort))
                                         .firstMap();
                    break;
                case DOWN:
                    target = targetSelect.where(x ->
                                                        x.and(FUC_Fields.parentId,
                                                              FilterCompare.Eq,
                                                              current.get(FUC_Fields.parentId))
                                                         .and(FUC_Fields.sort,
                                                              FilterCompare.Gt,
                                                              current.get(FUC_Fields.sort)))
                                         .orderBy(x -> x.orderBy(FUC_Fields.sort))
                                         .firstMap();
                    break;
                case LOW:
                    target = targetSelect.where(
                                                 x -> x.and(FUC_Fields.parentId,
                                                            FilterCompare.Eq,
                                                            current.get(FUC_Fields.parentId)))
                                         .orderBy(x -> x.orderByDescending(FUC_Fields.sort))
                                         .firstMap();
                    break;
                default:
                    throw new BusinessException(String.format("不支持此排序方法%s",
                                                              data.getMethod()));
            }

            //目标为空，判定为无意义的操作
            if (target == null)
                return;

            if (repository_Key.updateDiy()
                              .set(FUC_Fields.sort,
                                   current.get(FUC_Fields.sort))
                              .where(x -> x.and(FUC_Fields.id,
                                                FilterCompare.Eq,
                                                target.get(FUC_Fields.id)))
                              .executeAffrows() < 0
                    || repository_Key.updateDiy()
                                     .set(FUC_Fields.sort,
                                          target.get(FUC_Fields.sort))
                                     .where(x -> x.and(FUC_Fields.id,
                                                       FilterCompare.Eq,
                                                       current.get(FUC_Fields.id)))
                                     .executeAffrows() < 0)
                throw new BusinessException("排序失败");
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("排序失败",
                                        ex);
        }
    }

    @Override
    @Transactional
    public void dragSort(TreeDragSortDTO<String> data)
            throws
            BusinessException {
        try {
            //操作对象和目标对象相同，判定为无意义的操作
            if (data.getId()
                    .equals(data.getTargetId()))
                return;

            Map<String, Object> current = repository_Key.select()
                                                        .columns(FUC_Fields.id,
                                                                 FUC_Fields.parentId,
                                                                 FUC_Fields.sort)
                                                        .where(x -> x.and(FUC_Fields.id,
                                                                          FilterCompare.Eq,
                                                                          data.getId()))
                                                        .firstMap();

            if (current == null)
                throw new BusinessException("数据不存在或已被移除");

            Map<String, Object> target = repository_Key.select()
                                                       .columns(FUC_Fields.id,
                                                                FUC_Fields.rootId,
                                                                FUC_Fields.parentId,
                                                                FUC_Fields.level,
                                                                FUC_Fields.sort)
                                                       .where(x -> x.and(FUC_Fields.id,
                                                                         FilterCompare.Eq,
                                                                         data.getTargetId()))
                                                       .firstMap();

            if (target == null)
                throw new BusinessException("目标数据不存在");

            if (!data.getInside() && Objects.equals(current.get(FUC_Fields.parentId),
                                                    target.get(FUC_Fields.parentId))) {
                ISelect<CommonFileUploadConfig> targetNewSelect = repository_Key.select()
                                                                                .columns(FUC_Fields.id,
                                                                                         FUC_Fields.sort);
                //同层级排序
                Map<String, Object> targetNew;

                if (data.getAppend()) {
                    targetNew = targetNewSelect.where(x ->
                                                              x.and(FUC_Fields.parentId,
                                                                    FilterCompare.Eq,
                                                                    target.get(FUC_Fields.parentId))
                                                               .and(FUC_Fields.sort,
                                                                    FilterCompare.Eq,
                                                                    (int) target.get(FUC_Fields.sort) + 1))
                                               .firstMap();
                } else {
                    targetNew = targetNewSelect.where(x ->
                                                              x.and(FUC_Fields.parentId,
                                                                    FilterCompare.Eq,
                                                                    target.get(FUC_Fields.parentId))
                                                               .and(FUC_Fields.sort,
                                                                    FilterCompare.Eq,
                                                                    (int) target.get(FUC_Fields.sort) - 1))
                                               .firstMap();
                }

                if (repository_Key.updateDiy()
                                  .set(FUC_Fields.sort,
                                       current.get(FUC_Fields.sort))
                                  .where(x -> x.and(FUC_Fields.id,
                                                    FilterCompare.Eq,
                                                    targetNew.get(FUC_Fields.id)))
                                  .executeAffrows() < 0
                        || repository_Key.updateDiy()
                                         .set(FUC_Fields.sort,
                                              targetNew.get(FUC_Fields.sort))
                                         .where(x -> x.and(FUC_Fields.id,
                                                           FilterCompare.Eq,
                                                           current.get(FUC_Fields.id)))
                                         .executeAffrows() < 0)
                    throw new BusinessException("排序失败");
            } else {
                //异层级排序
                if (repository_Key.updateDiy()
                                  .set(FUC_Fields.sort,
                                       Integer.class,
                                       x -> x.operationWithValue(OperationSymbol.Reduce,
                                                                 1))
                                  .where(x ->
                                                 x.and(FUC_Fields.parentId,
                                                       FilterCompare.Eq,
                                                       current.get(FUC_Fields.parentId))
                                                  .and(FUC_Fields.sort,
                                                       FilterCompare.Gt,
                                                       current.get(FUC_Fields.sort)))
                                  .executeAffrows() < 0
                        || repository_Key.updateDiy()
                                         .set(FUC_Fields.sort,
                                              Integer.class,
                                              x -> x.operationWithValue(OperationSymbol.Plus,
                                                                        1))
                                         .where(x ->
                                                        x.and(FUC_Fields.parentId,
                                                              FilterCompare.Eq,
                                                              target.get(FUC_Fields.parentId))
                                                         .and(FUC_Fields.sort,
                                                              FilterCompare.Gt,
                                                              data.getInside()
                                                              ? 0
                                                              : (data.getAppend()
                                                                 ? target.get(FUC_Fields.sort)
                                                                 : ((int) current.get(FUC_Fields.sort) - 1))))
                                         .executeAffrows() < 0
                        || repository_Key.updateDiy()
                                         .set(FUC_Fields.sort,
                                              data.getInside()
                                              ? 1
                                              : (data.getAppend()
                                                 ? ((int) target.get(FUC_Fields.sort) + 1)
                                                 : target.get(FUC_Fields.sort)))
                                         .set(FUC_Fields.parentId,
                                              data.getInside()
                                              ? target.get(FUC_Fields.id)
                                              : target.get(FUC_Fields.parentId))
                                         .set(FUC_Fields.level,
                                              data.getInside()
                                              ? ((int) target.get(FUC_Fields.level) + 1)
                                              : target.get(FUC_Fields.level))
                                         .set(FUC_Fields.rootId,
                                              target.get(FUC_Fields.rootId))
                                         .where(x -> x.and(FUC_Fields.id,
                                                           FilterCompare.Eq,
                                                           current.get(FUC_Fields.id)))
                                         .executeAffrows() < 0)
                    throw new BusinessException("排序失败");
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("排序失败",
                                        ex);
        }
    }

    @Override
    public void enable(String id,
                       Boolean enable)
            throws
            BusinessException {
        try {
            if (repository_Key.updateDiy()
                              .set(FUC_Fields.enable,
                                   enable)
                              .where(x -> x.and(FUC_Fields.id,
                                                FilterCompare.Eq,
                                                id))
                              .executeAffrows() < 0)
                throw new Exception("数据库受影响行数有误");
        } catch (Exception ex) {
            throw new BusinessException(String.format("%s失败",
                                                      enable
                                                      ? "启用"
                                                      : "禁用"),
                                        ex);
        }
    }

    @Override
    public GetReferenceConfigFunUse_Types getConfigTypes(String code) {
        if (code.equals("default"))
            code = repository_Key.select()
                                 .where(x ->
                                                x.and(FUC_Fields.enable,
                                                      FilterCompare.Eq,
                                                      true)
                                                 .and(FUC_Fields.public_,
                                                      FilterCompare.Eq,
                                                      true))
                                 .orderBy(x -> x.orderBy(FUC_Fields.level))
                                 .first(FUC_Fields.code,
                                        String.class);
        String finalCode = code;

        return repository_Key.select()
                             .where(x ->
                                            x.and(FUC_Fields.code,
                                                  FilterCompare.Eq,
                                                  finalCode)
                                             .and(FUC_Fields.enable,
                                                  FilterCompare.Eq,
                                                  true))
                             .first(GetReferenceConfigFunUse_Types.class);
    }
}
