package top.lctr.naive.file.system.configures;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.extension.standard.authentication.IAuthenticationService;
import project.extension.standard.entity.DefaultEntityExtension;
import project.extension.standard.entity.IEntityExtension;

/**
 * 项目拓展配置
 *
 * @author LCTR
 * @date 2022-12-14
 */
@Configuration
public class ProjectExtensionConfigure {
    @Bean
    public IEntityExtension configIEntityExtension(IAuthenticationService authenticationService) {
        return new DefaultEntityExtension(authenticationService);
    }
}
