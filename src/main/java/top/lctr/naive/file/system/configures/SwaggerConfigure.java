package top.lctr.naive.file.system.configures;

import io.swagger.models.auth.In;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import top.lctr.naive.file.system.config.ServiceConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger3的接口配置
 *
 * @author LCTR
 * @date 2023-01-16
 */
@Configuration
@EnableConfigurationProperties({ServiceConfig.class})
public class SwaggerConfigure {
    public SwaggerConfigure(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    /**
     * 服务配置
     */
    private final ServiceConfig serviceConfig;

    /**
     * 反向代理路径
     */
    public static String pathMapping = "/";

    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                // 是否启用Swagger
                .enable(serviceConfig.getEnableSwagger())
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo(serviceConfig))
//                .groupName("全部接口")
                // 设置哪些接口暴露给Swagger展示
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描指定包中的swagger注解
                // .apis(RequestHandlerSelectors.basePackage("com.ruoyi.project.tool.swagger"))
//                 扫描所有
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                /* 设置安全模式，swagger可以设置访问token */
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .pathMapping(pathMapping);
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     */
    public static List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(new ApiKey("Authorization",
                                  "Authorization",
                                  In.HEADER.toValue()));
        return apiKeyList;
    }

    /**
     * 安全上下文
     */
    public static List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                               .securityReferences(defaultAuth())
                               .operationSelector(o -> o.requestMappingPattern()
                                                        .matches("/.*"))
                               .build());
        return securityContexts;
    }

    /**
     * 默认的安全上引用
     */
    public static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global",
                                                                       "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization",
                                                     authorizationScopes));
        return securityReferences;
    }

    /**
     * 添加摘要信息
     */
    public static ApiInfo apiInfo(ServiceConfig serviceConfig) {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                // 设置标题
                .title(String.format("%s 接口文档",
                                     serviceConfig.getName()))
                // 描述
                .description("用于上传和管理用户的文件")
                // 作者信息
                .contact(new Contact(serviceConfig.getName(),
                                     null,
                                     null))
                // 版本
                .version("版本号:" + serviceConfig.getVersion())
                .build();
    }
}
