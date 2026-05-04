package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphyra.apphub.api.platform.authorization.model.Token;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TokenResponseMapper {
    private final DateTimeUtil dateTimeUtil;

    TokenResponse create(RefreshToken refreshToken, String refreshTokenJwt, AccessTokenDto accessToken) {
        return TokenResponse.builder()
            .accessToken(Token.builder()
                .jwt(accessToken.getJwt())
                .expiration(dateTimeUtil.toEpochMillis(accessToken.getExpiration()))
                .path("/")
                .build())
            .refreshToken(Token.builder()
                .jwt(refreshTokenJwt)
                .expiration(dateTimeUtil.toEpochMillis(refreshToken.getExpiration()))
                .path("/api/authorization")
                .build())
            .build();
    }
}
