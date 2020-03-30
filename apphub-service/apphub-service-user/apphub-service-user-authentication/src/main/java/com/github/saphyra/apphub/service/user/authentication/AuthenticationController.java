package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.hithub.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.hithub.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.hithub.saphyra.apphub.api.user.authentication.server.UserAuthenticationController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
//TODO unit test
//TODO int test
//TODO api test
//TODO fe test
public class AuthenticationController implements UserAuthenticationController {
    private final LoginService loginService;
    private final Integer accessTokenCookieExpirationDays;

    public AuthenticationController(
        LoginService loginService,
        @Value("${accessToken.cookie.expirationDays}") Integer accessTokenCookieExpirationDays
    ) {
        this.loginService = loginService;
        this.accessTokenCookieExpirationDays = accessTokenCookieExpirationDays;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("LoginRequest arrived: {}", loginRequest);
        AccessToken accessToken = loginService.login(loginRequest);
        Integer expiration = accessToken.isPersistent() ? accessTokenCookieExpirationDays : null;
        log.info("Setting up accessToken cookie with accessTokenId {}, expiration {}", accessToken.getAccessTokenId(), expiration);
        return LoginResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .expirationDays(expiration)
            .build();
    }
}
