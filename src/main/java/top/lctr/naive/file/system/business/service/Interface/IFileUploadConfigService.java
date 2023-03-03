package top.lctr.naive.file.system.business.service.Interface;

import project.extension.mybatis.edge.extention.datasearch.TreeDataSearchDTO;
import project.extension.standard.datasort.DataSortDTO;
import project.extension.standard.datasort.TreeDragSortDTO;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.dto.fileUploadConfigDTO.*;

import java.util.Collection;

/**
 * 文件上传配置服务接口类
 *
 * @author LCTR
 * @date 2022-04-08
 */
public interface IFileUploadConfigService {
    /**
     * 树状列表数据
     *
     * @param dataSearch 搜索参数
     * @return 树状列表数据
     */
    java.util.List<TreeList> treeList(TreeDataSearchDTO dataSearch)
            throws
            BusinessException;

    /**
     * 详情数据
     *
     * @param id 主键
     * @return 详情数据
     */
    Detail detail(String id)
            throws
            BusinessException;

    /**
     * 配置数据
     *
     * @param code              编码
     * @param withTransactional 是否在事务下运行
     */
    Config config(String code,
                  boolean withTransactional)
            throws
            BusinessException;

    /**
     * 配置数据集合
     *
     * @param codes 编码集合
     */
    java.util.List<Config> configList(Collection<String> codes)
            throws
            BusinessException;

    /**
     * 新增
     *
     * @param data 数据
     */
    void create(Create data)
            throws
            BusinessException;

    /**
     * 获取编辑数据
     *
     * @param id 主键
     * @return 编辑数据
     */
    Edit edit(String id)
            throws
            BusinessException;

    /**
     * 编辑
     *
     * @param data 数据
     */
    void edit(Edit data)
            throws
            BusinessException;

    /**
     * 删除
     *
     * @param ids 主键集合
     */
    void delete(Collection<String> ids)
            throws
            BusinessException;

    /**
     * 排序
     *
     * @param data 数据
     */
    void sort(DataSortDTO<String> data)
            throws
            BusinessException;

    /**
     * 拖动排序
     *
     * @param data 数据
     */
    void dragSort(TreeDragSortDTO<String> data)
            throws
            BusinessException;

    /**
     * 启用/禁用
     *
     * @param id     主键
     * @param enable true：启用，false：禁用
     */
    void enable(String id,
                Boolean enable)
            throws
            BusinessException;

    /**
     * 获取配置类型信息
     *
     * @param code 编码
     * @return 类型信息
     */
    GetReferenceConfigFunUse_Types getConfigTypes(String code);
}
