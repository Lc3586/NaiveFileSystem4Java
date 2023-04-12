package top.lctr.naive.file.system.business.service.Implementation;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import project.extension.standard.authentication.AuthenticationInfo;
import project.extension.standard.authentication.IAuthenticationService;

import java.util.Optional;

/**
 * 身份验证服务
 *
 * @author LCTR
 * @date 2022-12-12
 */
@Service
@Scope("prototype")
public class AuthenticationService
        implements IAuthenticationService {

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Authentication getAuthentication() {
        return null;
    }

    @Override
    public AuthenticationInfo getOperator() {
        return new AuthenticationInfo();
    }

    @Override
    public Optional<AuthenticationInfo> tryGetOperator() {
        return Optional.empty();
    }

    @Override
    public boolean isAuthorized(String... permission) {
        return true;
    }
}
