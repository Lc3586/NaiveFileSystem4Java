package top.lctr.naive.file.system.configures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import project.extension.collections.CollectionsExtension;
import project.extension.openapi.annotations.OpenApiAllowAnonymous;
import project.extension.openapi.annotations.OpenApiGroup;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import top.lctr.naive.file.system.config.ServiceConfig;

import java.util.*;

/**
 * Swagger接口分组配置
 *
 * @author LCTR
 * @date 2023-01-16
 */
@Configuration
public class SwaggerGroupByConfigure {
    public SwaggerGroupByConfigure(ServiceConfig serviceConfig,
                                   ApplicationContext applicationContext) {
        this.serviceConfig = serviceConfig;
        this.applicationContext = applicationContext;
    }

    /**
     * 客户端配置
     */
    private final ServiceConfig serviceConfig;

    private final ApplicationContext applicationContext;

    /**
     * 创建允许匿名访问的接口文档
     */
    @Bean
    public Docket createRestApiAllowAnonymous() {
        return new Docket(DocumentationType.OAS_30)
                .enable(serviceConfig.getEnableSwagger())
                .apiInfo(SwaggerConfigure.apiInfo(serviceConfig))
                .groupName("匿名接口")
                .select()
                .apis(x -> x.isAnnotatedWith(OpenApiAllowAnonymous.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(SwaggerConfigure.securitySchemes())
                .securityContexts(SwaggerConfigure.securityContexts())
                .pathMapping(SwaggerConfigure.pathMapping);
    }

    /**
     * 根据分组创建接口文档
     */
    @Autowired
    public void createRestApiGroupBy() {
        //获取所有分组
        List<String> names = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(
                                                                                            RequestMappingHandlerMapping.class)
                                                                                    .getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            //方法上的注解
            OpenApiGroup openApiGroup = handlerMethod.getMethodAnnotation(OpenApiGroup.class);
            //控制器上的注解
            if (openApiGroup == null)
                openApiGroup = handlerMethod.getBeanType()
                                            .getAnnotation(OpenApiGroup.class);

            if (openApiGroup != null && CollectionsExtension.anyPlus(openApiGroup.value())) {
                for (String name : openApiGroup.value()) {
                    if (!names.contains(name))
                        names.add(name);
                }
            }
        }

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        for (String name : names) {
            Docket docket = new Docket(DocumentationType.OAS_30)
                    .enable(serviceConfig.getEnableSwagger())
                    .apiInfo(SwaggerConfigure.apiInfo(serviceConfig))
                    .groupName(name)
                    .forCodeGeneration(true)
                    .select()
                    .apis(x -> {
                        //方法上的注解
                        Optional<OpenApiGroup> openApiGroup = x.findAnnotation(OpenApiGroup.class);
                        //控制器上的注解
                        if (!openApiGroup.isPresent())
                            openApiGroup = x.findControllerAnnotation(OpenApiGroup.class);

                        return openApiGroup.isPresent() && Arrays.asList(openApiGroup.get()
                                                                                     .value())
                                                                 .contains(name);
                    })
                    .paths(PathSelectors.any())
                    .build()
                    .securitySchemes(SwaggerConfigure.securitySchemes())
                    .securityContexts(SwaggerConfigure.securityContexts())
                    .pathMapping(SwaggerConfigure.pathMapping);

            //注入为单例
            defaultListableBeanFactory.registerSingleton(
                    String.format("createRestApiGroupBy-%s",
                                  docket.getGroupName()),
                    docket);
        }
    }
}
