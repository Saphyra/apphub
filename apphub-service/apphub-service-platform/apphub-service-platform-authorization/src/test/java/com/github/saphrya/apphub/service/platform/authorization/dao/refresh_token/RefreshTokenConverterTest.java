package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class RefreshTokenConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = USER_ID.toString();
    private static final String REFRESH_TOKEN_ID_STRING = REFRESH_TOKEN_ID.toString();
    private static final LocalDateTime ISSUED_AT = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime EXPIRATION = LocalDateTime.of(2026, 1, 2, 0, 0, 0);
    private static final long ISSUED_AT_EPOCH = 1000L;
    private static final long EXPIRATION_EPOCH = 2000L;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private RefreshTokenConverter underTest;

    @Test
    void processDomainConversion() {
        RefreshToken domain = RefreshToken.builder()
            .userId(USER_ID)
            .refreshTokenId(REFRESH_TOKEN_ID)
            .issuedAt(ISSUED_AT)
            .expiration(EXPIRATION)
            .rememberMe(true)
            .build();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);
        given(dateTimeUtil.toEpochSecond(ISSUED_AT)).willReturn(ISSUED_AT_EPOCH);
        given(dateTimeUtil.toEpochSecond(EXPIRATION)).willReturn(EXPIRATION_EPOCH);

        RefreshTokenEntity result = underTest.convertDomain(domain);

        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID_STRING);
        assertThat(result.getIssuedAt()).isEqualTo(ISSUED_AT_EPOCH);
        assertThat(result.getExpiration()).isEqualTo(EXPIRATION_EPOCH);
        assertThat(result.getRememberMe()).isTrue();
    }

    @Test
    void processEntityConversion() {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
            .userId(USER_ID_STRING)
            .refreshTokenId(REFRESH_TOKEN_ID_STRING)
            .issuedAt(ISSUED_AT_EPOCH)
            .expiration(EXPIRATION_EPOCH)
            .rememberMe(false)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(REFRESH_TOKEN_ID_STRING)).willReturn(REFRESH_TOKEN_ID);
        given(dateTimeUtil.fromEpochSecond(ISSUED_AT_EPOCH)).willReturn(ISSUED_AT);
        given(dateTimeUtil.fromEpochSecond(EXPIRATION_EPOCH)).willReturn(EXPIRATION);

        RefreshToken result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
        assertThat(result.getIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(result.getExpiration()).isEqualTo(EXPIRATION);
        assertThat(result.isRememberMe()).isFalse();
    }
}