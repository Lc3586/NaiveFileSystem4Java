package top.lctr.naive.file.system.configures;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.lctr.naive.file.system.business.handler.ChunkFileMergeHandler;
import top.lctr.naive.file.system.business.handler.FileRepairHandler;
import top.lctr.naive.file.system.business.handler.Word2PdfHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * 启动各个服务
 *
 * @author LCTR
 * @date 2023-03-03
 */
@Component
@Order(value = 1)
public class StartService
        implements ApplicationRunner {
    public StartService(ChunkFileMergeHandler chunkFileMergeHandler,
                        FileRepairHandler fileRepairHandler,
                        Word2PdfHandler word2PdfHandler) {
        this.chunkFileMergeHandler = chunkFileMergeHandler;
        this.fileRepairHandler = fileRepairHandler;
        this.word2PdfHandler = word2PdfHandler;
    }

    /**
     * 分片文件合并模块
     */
    private final ChunkFileMergeHandler chunkFileMergeHandler;

    /**
     * 文件信息修复模块
     */
    private final FileRepairHandler fileRepairHandler;

    /**
     * Word文档转PDF文档模块
     */
    private final Word2PdfHandler word2PdfHandler;

    /**
     * 运行各个服务
     *
     * @param args 参数
     */
    @Override
    public void run(ApplicationArguments args) {
        CompletableFuture.runAsync(chunkFileMergeHandler::start,
                                   Executors.newSingleThreadExecutor());
        CompletableFuture.runAsync(fileRepairHandler::start,
                                   Executors.newSingleThreadExecutor());
        CompletableFuture.runAsync(word2PdfHandler::start,
                                   Executors.newSingleThreadExecutor());
    }
}
