package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphrya.apphub.service.platform.authorization.etc.AuthorizationClientProxy;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RefreshTokenService {
    private final TokenService tokenService;
    private final RefreshTokenDao refreshTokenDao;
    private final AuthorizationClientProxy authorizationClientProxy;
    private final TokenResponseMapper tokenResponseMapper;

    public TokenResponse refresh(String refreshToken) {
        RefreshToken parsedToken = tokenService.verifyRefreshToken(refreshToken);
        log.info("{} used refreshToken {}", parsedToken.getUserId(), parsedToken.getRefreshTokenId());

        refreshTokenDao.findByUserIdAndRefreshTokenId(parsedToken.getUserId(), parsedToken.getRefreshTokenId())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "RefreshToken " + parsedToken.getRefreshTokenId() + " of user " + parsedToken.getUserId() + " not found."));

        BiWrapper<String, RefreshToken> newRefreshToken = tokenService.createRefreshToken(parsedToken.getUserId(), parsedToken.isRememberMe());
        List<String> roles = authorizationClientProxy.getRoles(parsedToken.getUserId());
        AccessTokenDto accessToken = tokenService.createAccessToken(parsedToken.getUserId(), newRefreshToken.getEntity2().getRefreshTokenId(), roles);

        refreshTokenDao.delete(parsedToken.getUserId(), parsedToken.getRefreshTokenId());
        refreshTokenDao.save(newRefreshToken.getEntity2());

        return tokenResponseMapper.create(newRefreshToken.getEntity2(), newRefreshToken.getEntity1(), accessToken);
    }
}
