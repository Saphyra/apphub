package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LogoutService {
    private final TokenService tokenService;
    private final RefreshTokenDao refreshTokenDao;

    public void logout(String refreshToken) {
        RefreshToken parsedToken = tokenService.verifyRefreshToken(refreshToken);

        log.info("Logging out user {}", parsedToken.getUserId());

        refreshTokenDao.delete(parsedToken.getUserId(), parsedToken.getRefreshTokenId());
    }
}
