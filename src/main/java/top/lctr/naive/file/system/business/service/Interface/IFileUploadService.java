package top.lctr.naive.file.system.business.service.Interface;

import org.springframework.web.multipart.MultipartFile;
import project.extension.standard.exception.BusinessException;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadChunkFileResponse;
import top.lctr.naive.file.system.dto.fileUploadDTO.PreUploadFileResponse;
import top.lctr.naive.file.system.dto.personalFileDTO.PersonalFile;

/**
 * 文件上传服务接口类
 *
 * @author LCTR
 * @date 2022-04-08
 */
public interface IFileUploadService {
    /**
     * 预备上传文件
     *
     * @param configCode 上传配置编码
     * @param md5        文件MD5值
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param length     文件字节数
     * @param filename   文件重命名
     * @param section    是否分片处理
     * @param specs      分片文件规格
     * @param total      分片文件总数
     * @return 输出信息
     */
    PreUploadFileResponse preUploadFile(String configCode,
                                        String md5,
                                        String type,
                                        String extension,
                                        Long length,
                                        String filename,
                                        Boolean section,
                                        Integer specs,
                                        Integer total)
            throws
            BusinessException;

    /**
     * 预备上传分片文件
     *
     * @param file_md5 文件MD5值
     * @param md5      分片文件MD5值
     * @param index    分片文件索引
     * @param specs    分片文件规格
     * @param forced   强制上传
     * @return 输出信息
     */
    PreUploadChunkFileResponse preUploadChunkFile(String file_md5,
                                                  String md5,
                                                  Integer index,
                                                  Integer specs,
                                                  Boolean forced)
            throws
            BusinessException;

    /**
     * 单分片文件上传
     *
     * @param key  上传标识
     * @param md5  分片文件MD5值
     * @param file 分片文件
     */
    void singleChunkFile(String key,
                         String md5,
                         MultipartFile file)
            throws
            BusinessException;

    /**
     * 单分片文件上传
     *
     * @param key 上传标识
     * @param md5 分片文件MD5值
     */
    void singleChunkFileByArrayBuffer(String key,
                                      String md5)
            throws
            BusinessException;

    /**
     * 分片文件全部上传完毕
     *
     * @param configCode 上传配置编码
     * @param file_md5   文件MD5值
     * @param specs      分片文件规格
     * @param total      分片文件总数
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param filename   文件重命名
     * @return 个人文件信息
     */
    PersonalFile uploadChunkFileFinished(String configCode,
                                         String file_md5,
                                         Integer specs,
                                         Integer total,
                                         String type,
                                         String extension,
                                         String filename)
            throws
            BusinessException;

    /**
     * 通过Base64字符串上传单个文件
     *
     * @param configCode 上传配置编码
     * @param base64     Base64字符串
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param filename   文件重命名
     * @return 个人文件信息
     */
    PersonalFile singleFileFromBase64(String configCode,
                                      String base64,
                                      String type,
                                      String extension,
                                      String filename)
            throws
            BusinessException;

    /**
     * 通过外链上传单个文件
     *
     * @param url      外链地址
     * @param filename 文件重命名
     * @param download 是否下载资源
     * @return 个人文件信息
     */
    PersonalFile singleFileFromUrl(String url,
                                   String filename,
                                   Boolean download)
            throws
            BusinessException;

    /**
     * 通过外链上传单个文件
     *
     * @param configCode 上传配置编码
     * @param url        外链地址
     * @param filename   文件重命名
     * @param download   是否下载资源
     * @return 个人文件信息
     */
    PersonalFile singleFileFromUrl(String configCode,
                                   String url,
                                   String filename,
                                   Boolean download)
            throws
            BusinessException;

    /**
     * 上传单个文件
     *
     * @param configCode 上传配置编码
     * @param filename   文件重命名
     * @param file       文件
     * @return 个人文件信息
     */
    PersonalFile singleFile(String configCode,
                            String filename,
                            MultipartFile file)
            throws
            BusinessException;

    /**
     * 上传单个文件
     *
     * @param configCode 上传配置编码
     * @param type       文件类型
     * @param extension  文件拓展名
     * @param filename   文件重命名
     * @return 个人文件信息
     */
    PersonalFile singleFileByArrayBuffer(String configCode,
                                         String type,
                                         String extension,
                                         String filename)
            throws
            BusinessException;
}
