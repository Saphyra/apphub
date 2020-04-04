package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.api.user.authentication.server.UserAuthenticationController;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Slf4j
//TODO int test
//TODO api test
//TODO fe test
public class AuthenticationController implements UserAuthenticationController {
    private final Integer accessTokenCookieExpirationDays;
    private final LoginService loginService;
    private final ValidAccessTokenQueryService validAccessTokenQueryService;

    @Builder
    public AuthenticationController(
        LoginService loginService,
        @Value("${accessToken.cookie.expirationDays}") Integer accessTokenCookieExpirationDays,
        ValidAccessTokenQueryService validAccessTokenQueryService
    ) {
        this.loginService = loginService;
        this.accessTokenCookieExpirationDays = accessTokenCookieExpirationDays;
        this.validAccessTokenQueryService = validAccessTokenQueryService;
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

    @Override
    public ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(UUID accessTokenId) {
        return validAccessTokenQueryService.findByAccessTokenId(accessTokenId)
            .map(this::convert)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private InternalAccessTokenResponse convert(AccessToken accessToken) {
        return InternalAccessTokenResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .userId(accessToken.getUserId())
            .build();
    }
}
