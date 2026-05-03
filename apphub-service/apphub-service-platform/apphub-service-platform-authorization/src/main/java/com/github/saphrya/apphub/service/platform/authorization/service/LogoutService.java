package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.EventGatewayProxy;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LogoutService {
    private final TokenService tokenService;
    private final RefreshTokenDao refreshTokenDao;
    private final EventGatewayProxy eventGatewayProxy;

    public void logout(String refreshToken, String accessTokenString) {
        RefreshToken parsedToken = tokenService.verifyRefreshToken(refreshToken);

        log.info("Logging out user {}", parsedToken.getUserId());

        refreshTokenDao.delete(parsedToken.getUserId(), parsedToken.getRefreshTokenId());

        if (!isBlank(accessTokenString)) {
            AccessToken accessToken = tokenService.parseAccessToken(accessTokenString);

            eventGatewayProxy.sendAccessTokensInvalidatedEvent(accessToken.getAccessTokenId());
        }
    }

    public void deactivateAllSessions(UUID userId) {
        List<UUID> deactivatedTokens = refreshTokenDao.deleteByUserId(userId);
        eventGatewayProxy.sendRefreshTokensInvalidatedEvent(deactivatedTokens);
    }

    public void invalidateAllAccessTokens(UUID userId) {
        List<UUID> deactivatedTokens = refreshTokenDao.getByUserId(userId)
            .stream()
            .map(RefreshToken::getRefreshTokenId)
            .toList();
        eventGatewayProxy.sendRefreshTokensInvalidatedEvent(deactivatedTokens);
    }
}
