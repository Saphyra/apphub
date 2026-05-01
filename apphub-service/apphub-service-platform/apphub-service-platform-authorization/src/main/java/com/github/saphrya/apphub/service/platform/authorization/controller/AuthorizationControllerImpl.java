package com.github.saphrya.apphub.service.platform.authorization.controller;

import com.github.saphrya.apphub.service.platform.authorization.service.LoginService;
import com.github.saphrya.apphub.service.platform.authorization.service.LogoutService;
import com.github.saphrya.apphub.service.platform.authorization.service.RefreshTokenService;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.api.platform.authorization.server.AuthorizationController;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;

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
    public void logout(String refreshToken) {
        logoutService.logout(refreshToken);
    }

    @Override
    public TokenResponse refresh(@CookieValue(Constants.REFRESH_TOKEN_COOKIE) String refreshToken) {
        return refreshTokenService.refresh(refreshToken);
    }
}
