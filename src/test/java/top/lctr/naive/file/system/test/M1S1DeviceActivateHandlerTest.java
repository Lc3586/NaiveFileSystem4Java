package top.lctr.naive.file.system.test;

import org.junit.jupiter.api.*;
import project.extension.ioc.IOCExtension;
import top.lctr.naive.file.system.business.handler.ChunkFileMergeHandler;
import top.lctr.naive.file.system.common.HandlerExtension;

/**
 * 1.1、分片文件合并模块测试
 *
 * @author LCTR
 * @date 2023-02-14
 */
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class M1S1DeviceActivateHandlerTest {
    /**
     * 分片文件合并模块
     */
    private static ChunkFileMergeHandler chunkFileMergeHandler;

    /**
     * 设置
     */
    @BeforeEach
    public void setup() {
        chunkFileMergeHandler = IOCExtension.applicationContext.getBean(ChunkFileMergeHandler.class);
    }

    /**
     * 清理
     */
    @AfterAll
    public static void cleanUp()
            throws
            Throwable {
        System.out.println("正在清理");

        chunkFileMergeHandler.shutDown();
        HandlerExtension.wait2Idle(chunkFileMergeHandler.getName(),
                                   () -> chunkFileMergeHandler.getState(),
                                   () -> chunkFileMergeHandler.getConcurrentTaskCount(),
                                   () -> chunkFileMergeHandler.getScheduleTaskCount() - 1);
        System.out.println("设备激活模块已关闭");

        System.out.println("清理完成");
    }

    /**
     * 检查启动状态
     */
    @Test
    @DisplayName("1.1.1、检查启动状态")
    @Order(1001001)
    public void M1S1P1()
            throws
            Throwable {
        HandlerExtension.wait2Start(chunkFileMergeHandler.getName(),
                                    () -> chunkFileMergeHandler.getState());
    }

    /**
     * 检查合并结果
     */
    @Test
    @DisplayName("1.1.2、检查合并结果")
    @Order(1001002)
    public void M1S1P2()
            throws
            Throwable {
        HandlerExtension.wait2Idle(chunkFileMergeHandler.getName(),
                                   () -> chunkFileMergeHandler.getState(),
                                   () -> chunkFileMergeHandler.getConcurrentTaskCount(),
                                   () -> chunkFileMergeHandler.getScheduleTaskCount() - 1);
    }
}
