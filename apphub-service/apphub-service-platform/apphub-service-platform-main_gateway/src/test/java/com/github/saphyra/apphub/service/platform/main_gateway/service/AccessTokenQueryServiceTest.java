package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenQueryServiceTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AccessTokenIdConverter accessTokenIdConverter;

    @Mock
    private AccessTokenCache accessTokenCache;

    @InjectMocks
    private AccessTokenQueryService underTest;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void getAccessToken() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenCache.get(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessTokenResponse));

        assertThat(underTest.getAccessToken(ACCESS_TOKEN_ID_STRING)).contains(accessTokenResponse);
    }

    @Test
    public void error() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenCache.get(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        assertThat(underTest.getAccessToken(ACCESS_TOKEN_ID_STRING)).isEmpty();
    }
}