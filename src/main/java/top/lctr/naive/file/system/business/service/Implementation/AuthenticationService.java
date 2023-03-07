package top.lctr.naive.file.system.business.service.Implementation;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import project.extension.standard.authentication.IAuthenticationService;
import project.extension.standard.authentication.Operator;

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
    public Operator getOperator() {
        return new Operator();
    }

    @Override
    public Optional<Operator> tryGetOperator() {
        return Optional.empty();
    }

    @Override
    public boolean isAuthorized(String... permission) {
        return true;
    }
}
