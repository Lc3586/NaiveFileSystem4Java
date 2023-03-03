package top.lctr.naive.file.system.business.service.Interface;

import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_ForMerge;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_Indices;
import top.lctr.naive.file.system.entity.common.CommonChunkFile;

import java.util.Collection;
import java.util.Date;

/**
 * 分片文件信息服务接口类
 *
 * @author LCTR
 * @date 2022-12-08
 */
public interface IChunkFileService {
    /**
     * 获取文件状态信息
     *
     * @param md5   文件MD5校验值
     * @param specs 分片文件规格
     * @return 文件状态信息
     */
    FunUse_FileState getFileState(String md5,
                                  Integer specs);

    /**
     * 分片文件已上传数量
     *
     * @param file_md5 文件MD5校验值
     * @param specs    分片文件规格
     * @return 已上传数量
     */
    Long chunkFileAlreadyCount(String file_md5,
                               Integer specs);

    /**
     * 获取能用于合并的分片文件信息集合
     *
     * @param file_md5 文件MD5校验值
     * @param specs    分片文件规格
     * @return 用于合并的分片文件信息集合
     */
    java.util.List<FunUse_ForMerge> chunkFileAlreadyList(String file_md5,
                                                         Integer specs);

    /**
     * 获取已上传的分片文件里各个索引对应的分片文件数量
     *
     * @param file_md5 文件MD5校验值
     * @param specs    分片文件规格
     * @return 各个索引对应的分片文件数量
     */
    java.util.List<FunUse_Indices> chunkFileIndicesList(String file_md5,
                                                        Integer specs);

    /**
     * 最后上传的分片文件的创建时间
     *
     * @param file_md5 文件MD5校验值
     * @param specs    分片文件规格
     * @return 已上传数量
     */
    Date lastUploadedChunkFileCreateTime(String file_md5,
                                         Integer specs);

    /**
     * 新增
     *
     * @param taskKey  任务标识
     * @param file_md5 文件MD5值
     * @param md5      分片文件MD5值
     * @param index    分片文件索引
     * @param specs    分片文件规格
     * @param path     路径
     */
    CommonChunkFile create(String taskKey,
                           String file_md5,
                           String md5,
                           int index,
                           int specs,
                           String path)
            throws
            BusinessException;

    /**
     * 更新
     *
     * @param taskKey      任务标识
     * @param md5          分片文件MD5值
     * @param bytes        字节数
     * @param relativePath 相对路径
     */
    void update(String taskKey,
                String md5,
                Long bytes,
                String relativePath)
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
     * 清理
     *
     * @param file_md5 文件MD5值
     * @param specs    分片文件规格
     */
    void clear(String file_md5,
               int specs)
            throws
            BusinessException;

    /**
     * 清理
     *
     * @param chunkFiles 分片文件集合
     */
    void clear(Collection<FunUse_ForMerge> chunkFiles)
            throws
            BusinessException;

    /**
     * 获取资源文件目录绝对路径
     */
    String getWWWRootDirectory();

    /**
     * 获取文件的绝对路径
     *
     * @param path 文件相对路径
     */
    String getFilePath(String path);
}
