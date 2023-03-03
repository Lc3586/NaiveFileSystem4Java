package top.lctr.naive.file.system.business.service.Interface;

import project.extension.file.VideoInfo;
import project.extension.mybatis.edge.extention.datasearch.DataSearchDTO;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.dto.chunkFileDTO.FunUse_FileState;
import top.lctr.naive.file.system.dto.fileDTO.FileInfo;
import top.lctr.naive.file.system.dto.fileDTO.LibraryInfo;
import top.lctr.naive.file.system.entity.common.CommonFile;

import java.util.Collection;

/**
 * 文件信息服务接口类
 *
 * @author LCTR
 * @date 2022-12-08
 */
public interface IFileService {
    /**
     * 列表数据
     *
     * @param dataSearch 搜索参数
     * @return 树状列表数据
     */
    java.util.List<FileInfo> list(DataSearchDTO dataSearch)
            throws
            BusinessException;

    /**
     * 获取待修复的文件Id集合
     *
     * @return 主键集合
     */
    java.util.List<String> getUnrepairedIdList();

    /**
     * 获取未转换为Pdf文件的文件主键集合
     *
     * @return 文件主键集合
     */
    java.util.List<String> getUnConvert2PdfIdList();

    /**
     * 详情数据
     *
     * @param id 主键
     * @return 详情数据
     */
    FileInfo detail(String id)
            throws
            BusinessException;

    /**
     * 详情数据集合
     *
     * @param ids 主键集合
     */
    java.util.List<FileInfo> detailList(Collection<String> ids)
            throws
            BusinessException;

    /**
     * 获取文件状态信息
     *
     * @param md5 文件MD5校验值
     * @return 文件状态信息
     */
    FunUse_FileState getFileState(String md5);

    /**
     * 批量更新文件状态信息
     *
     * @param md5       文件MD5校验值
     * @param fileState 文件状态
     * @param path      文件路径
     */
    void updateFileState(String md5,
                         String fileState,
                         String path);

    /**
     * 获取待修复的文件信息Id集合
     *
     * @return 文件信息Id集合
     */
    java.util.List<String> getRepairIdList();

    /**
     * 获取
     *
     * @param id 主键
     * @return 文件状态信息
     */
    CommonFile get(String id);

    /**
     * 更新
     *
     * @param file 文件信息
     */
    void update(CommonFile file);

    /**
     * 新增
     *
     * @param md5          文件MD5值
     * @param name         名称
     * @param extension    文件扩展名
     * @param contentType  内容类型
     * @param bytes        字节数
     * @param relativePath 文件相对路径
     * @param storageType  存储类型
     * @param state        状态
     */
    CommonFile create(
            String md5,
            String name,
            String extension,
            String contentType,
            Long bytes,
            String relativePath,
            String storageType,
            String state)
            throws
            BusinessException;

    /**
     * 批量更新文件状态信息
     *
     * @param md5          文件MD5校验值
     * @param name         名称
     * @param extension    文件扩展名
     * @param contentType  内容类型
     * @param bytes        字节数
     * @param relativePath 文件相对路径
     * @param storageType  存储类型
     * @param state        状态
     */
    void update(String md5,
                String name,
                String extension,
                String contentType,
                Long bytes,
                String relativePath,
                String storageType,
                String state,
                boolean doNotUpdateAvailableFile)
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
     * 预览
     *
     * @param id     主键
     * @param width  宽度
     * @param height 高度
     * @param time   视频的时间轴位置（默认值：00:00:00.0010000）
     */
    void preview(String id,
                 Integer width,
                 Integer height,
                 String time)
            throws
            BusinessException;

    /**
     * 浏览
     *
     * @param id 主键
     */
    void browse(String id)
            throws
            BusinessException;

    /**
     * 下载
     *
     * @param id     主键
     * @param rename 下载文件重命名
     */
    void download(String id,
                  String rename)
            throws
            BusinessException;

    /**
     * 保存文件至指定目录
     *
     * @param id          主键
     * @param saveDirPath 存储文件的文件夹路径
     * @param rename      下载文件重命名
     */
    void save(String id,
              String saveDirPath,
              String rename)
            throws
            BusinessException;

    /**
     * 文件类型
     *
     * @param extension 文件拓展名
     * @return 文件类型
     */
    String fileTypeByExtension(String extension)
            throws
            BusinessException;

    /**
     * 文件类型
     *
     * @param mime 文件内容类型
     * @return 文件类型
     */
    String fileTypeByMIME(String mime)
            throws
            BusinessException;

    /**
     * 文件类型对应的预览图
     *
     * @param extension 文件拓展名
     */
    void fileTypeImage(String extension)
            throws
            BusinessException;

    /**
     * 文件大小描述信息
     *
     * @param length 文件字节数
     * @return 文件类型
     */
    String fileSize(String length)
            throws
            BusinessException;

    /**
     * 视频文件信息
     *
     * @param id       主键
     * @param format   获取有关输入多媒体流的容器格式的信息
     * @param streams  获取有关输入多媒体流中包含的每个媒体流的信息
     * @param chapters 获取有关以该格式存储的章节的信息
     * @param programs 获取有关程序及其输入多媒体流中包含的流的信息
     * @param version  获取与程序版本有关的信息、获取与库版本有关的信息、获取与程序和库版本有关的信息
     * @return 视频信息
     */
    VideoInfo videoInfo(String id,
                        Boolean format,
                        Boolean streams,
                        Boolean chapters,
                        Boolean programs,
                        Boolean version)
            throws
            BusinessException;

    /**
     * 文件库信息
     *
     * @return 文件库信息
     */
    java.util.List<LibraryInfo> libraryInfo()
            throws
            BusinessException;

    /**
     * 所有文件类型
     *
     * @return 文件类型集合
     */
    java.util.List<String> fileTypes()
            throws
            BusinessException;

    /**
     * 所有文件存储类型
     *
     * @return 文件存储类型集合
     */
    java.util.List<String> storageTypes()
            throws
            BusinessException;

    /**
     * 所有文件状态
     *
     * @return 文件状态集合
     */
    java.util.List<String> fileStates()
            throws
            BusinessException;

    /**
     * 获取资源文件目录绝对路径
     */
    String getWWWRootDirectory();

    /**
     * 获取文件状态图存储路径根目录绝对路径
     */
    String getFileStateDirectory();

    /**
     * 获取文件的绝对路径
     *
     * @param id 主键
     */
    String getFilePathById(String id);

    /**
     * 获取文件的绝对路径
     *
     * @param path 文件相对路径
     */
    String getFilePath(String path);

    /**
     * word文件转换为pdf文件
     *
     * @param id 主键
     * @return 文件信息
     */
    FileInfo word2PdfAndReturnFileInfo(String id)
            throws
            BusinessException;

    /**
     * word文件转换为pdf文件
     *
     * @param id 主键
     */
    void word2Pdf(String id)
            throws
            BusinessException;
}
