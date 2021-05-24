package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
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
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenIdConverter accessTokenIdConverter;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private UserAuthenticationApiClient authenticationApi;

    @InjectMocks
    private AccessTokenQueryService underTest;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void getAccessToken() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willReturn(accessTokenResponse);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        assertThat(underTest.getAccessToken(ACCESS_TOKEN_ID_STRING)).contains(accessTokenResponse);
    }

    @Test
    public void error() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willThrow(new RuntimeException());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        assertThat(underTest.getAccessToken(ACCESS_TOKEN_ID_STRING)).isEmpty();
    }
}