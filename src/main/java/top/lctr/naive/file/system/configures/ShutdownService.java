package top.lctr.naive.file.system.configures;

import org.springframework.stereotype.Component;
import top.lctr.naive.file.system.business.handler.ChunkFileMergeHandler;
import top.lctr.naive.file.system.business.handler.FileRepairHandler;
import top.lctr.naive.file.system.business.handler.Word2PdfHandler;

import javax.annotation.PreDestroy;

/**
 * 关闭各个服务
 *
 * @author LCTR
 * @date 2023-02-10
 */
@Component
public class ShutdownService {
    public ShutdownService(ChunkFileMergeHandler chunkFileMergeHandler,
                           FileRepairHandler fileRepairHandler,
                           Word2PdfHandler word2PdfHandler) {
        this.chunkFileMergeHandler = chunkFileMergeHandler;
        this.fileRepairHandler = fileRepairHandler;
        this.word2PdfHandler = word2PdfHandler;
    }

    /**
     * 分片文件合并任务处理类
     */
    private final ChunkFileMergeHandler chunkFileMergeHandler;

    /**
     * 文件信息修复处理类
     */
    private final FileRepairHandler fileRepairHandler;

    /**
     * Word文件自动转换Pdf文件处理类
     */
    private final Word2PdfHandler word2PdfHandler;

    /**
     * 关闭各个服务
     */
    @PreDestroy
    public void shutDown() {
        try {
            chunkFileMergeHandler.shutDown();
        } catch (Exception ignore) {

        }

        try {
            fileRepairHandler.shutDown();
        } catch (Exception ignore) {

        }

        try {
            word2PdfHandler.shutDown();
        } catch (Exception ignore) {

        }
    }
}
