package top.lctr.naive.file.system.configures;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.ContainerInitializer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.ServletContext;
import java.util.Collections;

/**
 * 集线器服务配置类
 *
 * @author LCTR
 * @date 2023-03-03
 */
@Configuration
public class HubServiceConfigure {
    @Bean
    public ServletRegistrationBean<AtmosphereServlet> atmosphereServlet1() {
        ServletRegistrationBean<AtmosphereServlet> registration = new ServletRegistrationBean<>(
                new AtmosphereServlet(),
                "/hub/*");

        registration.addInitParameter(ApplicationConfig.ANNOTATION_PACKAGE,
                                      "top.lctr.naive.file.system.hub");
        registration.addInitParameter(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS,
                                      "10");
        registration.setLoadOnStartup(0);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public EmbeddedAtmosphereInitializer atmosphereInitializer() {
        return new EmbeddedAtmosphereInitializer();
    }

    private static class EmbeddedAtmosphereInitializer
            extends ContainerInitializer
            implements ServletContextInitializer {

        @Override
        public void onStartup(ServletContext servletContext) {
            onStartup(Collections.emptySet(),
                      servletContext);
        }
    }
}
