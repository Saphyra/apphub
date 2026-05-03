package com.github.saphrya.apphub.service.platform.authorization.controller;

import com.github.saphrya.apphub.service.platform.authorization.service.LoginService;
import com.github.saphrya.apphub.service.platform.authorization.service.LogoutService;
import com.github.saphrya.apphub.service.platform.authorization.service.RefreshTokenService;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.api.platform.authorization.server.AuthorizationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static feign.Util.isBlank;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class AuthorizationControllerImpl implements AuthorizationController {
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        log.info("{} arrived", loginRequest);

        return loginService.login(loginRequest);
    }

    @Override
    public void logout(String refreshToken, String accessToken) {
        if (isBlank(refreshToken)) {
            return;
        }

        logoutService.logout(refreshToken, accessToken);
    }

    @Override
    public ResponseEntity<TokenResponse> refresh(String refreshToken) {
        if (isBlank(refreshToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(refreshTokenService.refresh(refreshToken), HttpStatus.OK);
    }

    @Override
    public void deactivateAllSessions(UUID userId) {
        log.info("Deactivating all sessions of user {}", userId);

        logoutService.deactivateAllSessions(userId);
    }

    @Override
    public void invalidateAllAccessTokens(UUID userId) {
        log.info("Invalidating all accessTokens of user {}", userId);

        logoutService.invalidateAllAccessTokens(userId);
    }
}
