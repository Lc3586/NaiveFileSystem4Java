package top.lctr.naive.file.system.business.service.Interface;

import project.extension.mybatis.edge.extention.datasearch.DataSearchDTO;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.dto.personalFileDTO.Edit;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;
import top.lctr.naive.file.system.entity.CommonPersonalFile;

import java.util.Collection;

/**
 * 个人文件信息服务接口类
 *
 * @author LCTR
 * @date 2022-04-08
 */
public interface IPersonalFileService {
    /**
     * 列表数据
     *
     * @param dataSearch 搜索参数
     * @return 列表数据
     */
    java.util.List<PersonalFile> list(DataSearchDTO dataSearch)
            throws
            BusinessException;

    /**
     * 详情数据（在事务下执行）
     *
     * @param id 主键
     * @return 详情数据
     */
    PersonalFile detail(String id)
            throws
            BusinessException;

    /**
     * 新增
     *
     * @param fileId     文件信息主键
     * @param configCode 上传配置
     * @param name       文件名
     * @param state      状态
     * @param createBy   创建者
     * @return Id
     */
    String find(String fileId,
                String configCode,
                String name,
                String state,
                String createBy)
            throws
            BusinessException;

    /**
     * 启用
     *
     * @param fileId     文件信息主键
     * @param just4state 只更新此状态的数据
     * @param state      更新为此状态
     */
    void changeState(String fileId,
                     String just4state,
                     String state)
            throws
            BusinessException;

    /**
     * 详情数据集合
     *
     * @param ids 主键集合
     */
    java.util.List<PersonalFile> detailList(Collection<String> ids)
            throws
            BusinessException;

    /**
     * 新增
     *
     * @param name      文件名
     * @param extension 文件拓展名
     * @param fileId    文件Id
     * @param state     状态
     * @return 个人文件信息
     */
    CommonPersonalFile create(String name,
                              String extension,
                              String fileId,
                              String state)
            throws
            BusinessException;

    /**
     * 新增
     *
     * @param configCode 上传配置编码
     * @param name       文件名
     * @param extension  文件拓展名
     * @param fileId     文件Id
     * @param state      状态
     * @return Id
     */
    String create(String configCode,
                  String name,
                  String extension,
                  String fileId,
                  String state)
            throws
            BusinessException;

    /**
     * 新增
     *
     * @param id         指定Id
     * @param configCode 上传配置编码
     * @param name       文件名
     * @param extension  文件拓展名
     * @param fileId     文件Id
     * @param state      状态
     */
    void create(String id,
                String configCode,
                String name,
                String extension,
                String fileId,
                String state)
            throws
            BusinessException;

    /**
     * 重命名
     *
     * @param id       主键
     * @param fileName 文件名
     */
    void rename(String id,
                String fileName)
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
     * 预览
     *
     * @param id     主键
     * @param width  宽度
     * @param height 高度
     * @param time   视频的时间轴位置（示例值：1:59:59）
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
     * 保存文件至指定目录
     *
     * @param info        个人文件信息
     * @param saveDirPath 存储文件的文件夹路径
     * @param rename      下载文件重命名
     */
    void save(PersonalFile info,
              String saveDirPath,
              String rename)
            throws
            BusinessException;

    /**
     * 获取文件的绝对路径
     *
     * @param id 主键
     */
    String getFilePathById(String id)
            throws
            BusinessException;

    /**
     * word文件转换为pdf文件
     *
     * @param id 主键
     * @return 个人文件信息
     */
    PersonalFile word2Pdf(String id)
            throws
            BusinessException;
}
