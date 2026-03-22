package com.github.saphyra.apphub.service.user.authentication.dao;

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
public class AccessTokenConverterTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final String USER_ID_STRING = "user-id";
    private static final LocalDateTime LAST_ACCESS = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String LAST_VISITED_PAGE = "last-visited-page";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AccessTokenConverter underTest;

    @Test
    public void convertEntity() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_STRING)
            .userId(USER_ID_STRING)
            .persistent(true)
            .lastAccess(LAST_ACCESS)
            .build();

        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        AccessToken result = underTest.convertEntity(entity);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.isPersistent()).isTrue();
        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS);
    }

    @Test
    public void convertDomain() {
        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID)
            .userId(USER_ID)
            .persistent(true)
            .lastAccess(LAST_ACCESS)
            .build();

        given(uuidConverter.convertDomain(ACCESS_TOKEN_ID)).willReturn(ACCESS_TOKEN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        AccessTokenEntity result = underTest.convertDomain(accessToken);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.isPersistent()).isTrue();
        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS);
    }
}