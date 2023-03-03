package top.lctr.naive.file.system;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import top.lctr.naive.file.system.application.SpringBootTestApplication;
import top.lctr.naive.file.system.test.M1S0JasyptTest;
import top.lctr.naive.file.system.test.M1S1DeviceActivateHandlerTest;

/**
 * 测试合集
 *
 * @author LCTR
 * @date 2023-02-08
 */
@DisplayName("测试合集")
@SpringBootTest(classes = SpringBootTestApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TestCollection {
    @Nested
    @Order(1000)
    @DisplayName("1.0.Jasypt加密解密测试")
    public class ForM1S0JasyptTest
            extends M1S0JasyptTest {
    }

    @Nested
    @Order(1001)
    @DisplayName("1.1.设备激活模块测试")
    public class ForM1S1DeviceActivateHandlerTest
            extends M1S1DeviceActivateHandlerTest {
    }
}
