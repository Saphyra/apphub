package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphyra.apphub.api.platform.main_gateway.client.MainGatewayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutService {
    private final TokenService tokenService;
    private final RefreshTokenDao refreshTokenDao;
    private final MainGatewayClient mainGatewayClient;

    public void logout(String refreshToken, String accessTokenString) {
        RefreshToken parsedToken = tokenService.verifyRefreshToken(refreshToken);

        log.info("Logging out user {}", parsedToken.getUserId());

        refreshTokenDao.delete(parsedToken.getUserId(), parsedToken.getRefreshTokenId());
        mainGatewayClient.invalidateRefreshTokens(List.of(parsedToken.getRefreshTokenId()));

        if (!isBlank(accessTokenString)) {
            tokenService.parseAccessToken(accessTokenString)
                .ifPresent(accessToken -> mainGatewayClient.invalidateAccessToken(accessToken.getAccessTokenId()));
        }
    }

    public void invalidateAllRefreshTokens(UUID userId) {
        List<UUID> invalidatedRefreshTokenIds = refreshTokenDao.deleteByUserId(userId);

        mainGatewayClient.invalidateRefreshTokens(invalidatedRefreshTokenIds);
    }

    public void invalidateAllAccessTokens(UUID userId) {
        List<UUID> invalidatedRefreshTokenIds = refreshTokenDao.getByUserId(userId)
            .stream()
            .map(RefreshToken::getRefreshTokenId)
            .toList();
        mainGatewayClient.invalidateRefreshTokens(invalidatedRefreshTokenIds);
    }
}
