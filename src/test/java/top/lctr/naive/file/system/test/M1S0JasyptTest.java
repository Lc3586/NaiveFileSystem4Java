package top.lctr.naive.file.system.test;

import org.junit.jupiter.api.*;
import project.extension.cryptography.JasyptUtils;

/**
 * Jasypt加密解密测试
 *
 * @author LCTR
 * @date 2022-12-14
 */
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class M1S0JasyptTest {
    /**
     * 加密和解密
     */
    @Test
    @DisplayName("加密和解密")
    @Order(1000)
    public void encryptAndDecrypt()
            throws
            Throwable {
        String password = JasyptUtils.getPassword();
        System.out.printf("密码：%s%n",
                          password);
        Assertions.assertNotEquals(null,
                                   password,
                                   "未获取到密码，请检查配置");

        String text = "123456";
        System.out.printf("明文：%s%n",
                          text);

        String data1 = JasyptUtils.encrypt(text,
                                           password);
        System.out.printf("密文1：%s%n",
                          data1);

        String textDe1 = JasyptUtils.decrypt(data1,
                                             password);
        System.out.printf("解密1：%s%n",
                          textDe1);
        Assertions.assertEquals(text,
                                textDe1,
                                "解密数据和原始明文数据不一致");

        String data2 = String.format("ENC(%s)",
                                     data1);
        System.out.printf("密文2：%s%n",
                          data2);

        String textDe2 = JasyptUtils.decrypt(data1,
                                             password);
        System.out.printf("解密2：%s%n",
                          textDe2);
        Assertions.assertEquals(text,
                                textDe2,
                                "解密数据和原始明文数据不一致");

        System.out.println("加解密结果正确");
    }
}
