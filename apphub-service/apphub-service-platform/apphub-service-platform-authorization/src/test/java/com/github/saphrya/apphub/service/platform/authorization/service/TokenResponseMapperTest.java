package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenResponseMapperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final LocalDateTime REFRESH_TOKEN_EXPIRATION = LocalDateTime.of(2026, 1, 2, 0, 0, 0);
    private static final LocalDateTime ACCESS_TOKEN_EXPIRATION = LocalDateTime.of(2026, 1, 1, 1, 0, 0);
    private static final long REFRESH_TOKEN_EXPIRATION_EPOCH = 2000L;
    private static final long ACCESS_TOKEN_EXPIRATION_EPOCH = 1000L;
    private static final String REFRESH_TOKEN_JWT = "refresh-jwt";
    private static final String ACCESS_TOKEN_JWT = "access-jwt";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private TokenResponseMapper underTest;

    @Test
    void create() {
        RefreshToken refreshToken = RefreshToken.builder()
            .userId(USER_ID)
            .refreshTokenId(REFRESH_TOKEN_ID)
            .issuedAt(LocalDateTime.now())
            .expiration(REFRESH_TOKEN_EXPIRATION)
            .build();

        AccessTokenDto accessToken = AccessTokenDto.builder()
            .jwt(ACCESS_TOKEN_JWT)
            .expiration(ACCESS_TOKEN_EXPIRATION)
            .build();

        given(dateTimeUtil.toEpochMillis(ACCESS_TOKEN_EXPIRATION)).willReturn(ACCESS_TOKEN_EXPIRATION_EPOCH);
        given(dateTimeUtil.toEpochMillis(REFRESH_TOKEN_EXPIRATION)).willReturn(REFRESH_TOKEN_EXPIRATION_EPOCH);

        TokenResponse result = underTest.create(refreshToken, REFRESH_TOKEN_JWT, accessToken);

        assertThat(result.getAccessToken().getJwt()).isEqualTo(ACCESS_TOKEN_JWT);
        assertThat(result.getAccessToken().getExpiration()).isEqualTo(ACCESS_TOKEN_EXPIRATION_EPOCH);
        assertThat(result.getAccessToken().getPath()).isEqualTo("/");

        assertThat(result.getRefreshToken().getJwt()).isEqualTo(REFRESH_TOKEN_JWT);
        assertThat(result.getRefreshToken().getExpiration()).isEqualTo(REFRESH_TOKEN_EXPIRATION_EPOCH);
        assertThat(result.getRefreshToken().getPath()).isEqualTo("/api/authorization");
    }
}