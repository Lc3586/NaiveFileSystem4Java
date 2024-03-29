package top.lctr.naive.file.system.business.service.Interface;

import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.entity.CommonChunkFileMergeTask;

/**
 * 分片文件合并任务服务接口类
 *
 * @author LCTR
 * @date 2022-12-08
 */
public interface IChunkFileMergeTaskService {
    /**
     * 新增
     *
     * @param md5         文件MD5值
     * @param contentType 文件内容类型
     * @param extension   文件拓展名
     * @param name        文件名(不包括拓展名)
     * @param specs       分片规格
     * @param total       分片总数
     * @param state       状态
     * @return 文件状态信息
     */
    CommonChunkFileMergeTask create(String md5,
                                    String contentType,
                                    String extension,
                                    String name,
                                    int specs,
                                    int total,
                                    String state)
            throws
            BusinessException;

    /**
     * 更新任务
     *
     * @param task 任务信息
     */
    void update(CommonChunkFileMergeTask task);

    /**
     * 获取
     *
     * @param id 主键
     * @return 文件状态信息
     */
    CommonChunkFileMergeTask get(String id);

    /**
     * 获取未完成的任务Id集合
     *
     * @return 主键集合
     */
    java.util.List<String> getUnfinishedIdList();

    /**
     * 获取待清理的任务Id集合
     *
     * @return 主键集合
     */
    java.util.List<String> getUnclearedIdList();

    /**
     * 是否正在上传分片文件
     *
     * @param md5   文件MD5校验值
     * @param specs 分片文件规格
     */
    Boolean isUploading(String md5,
                        Integer specs);

    /**
     * 最后一个分片合并任务的当前分片文件索引
     *
     * @param md5   文件MD5校验值
     * @param specs 分片文件规格
     * @return 当前分片文件索引
     */
    Integer lastCurrentIndex(String md5,
                             Integer specs);

    /**
     * 任务是否已存在
     * <p>未失败的任务</p>
     *
     * @param md5   文件MD5校验值
     * @param specs 分片文件规格
     * @param total 分片文件总数
     */
    Boolean isAlreadyExist(String md5,
                           Integer specs,
                           Integer total);

    /**
     * 获取已存在的任务
     * <p>未失败的任务</p>
     *
     * @param md5   文件MD5校验值
     * @param specs 分片文件规格
     * @param total 分片文件总数
     */
    CommonChunkFileMergeTask getAlreadyTask(String md5,
                                            Integer specs,
                                            Integer total);

    /**
     * 任务是否已失效
     *
     * @param id 主键
     * @return true 已失效
     */
    Boolean isExpire(String id);
}
