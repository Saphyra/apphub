package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Before;
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
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";

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

    @Before
    public void setUp() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(commonConfigProperties.getDefaultLocale()).willReturn(TestConstants.DEFAULT_LOCALE);
    }

    @Test
    public void invalidAccessTokenId() {
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.empty());

        Optional<InternalAccessTokenResponse> result = underTest.getAccessToken(ACCESS_TOKEN_ID_STRING);

        assertThat(result).isEmpty();
    }

    @Test
    public void getAccessToken() {
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID, TestConstants.DEFAULT_LOCALE)).willReturn(accessTokenResponse);

        Optional<InternalAccessTokenResponse> result = underTest.getAccessToken(ACCESS_TOKEN_ID_STRING);

        assertThat(result).contains(accessTokenResponse);
    }

    @Test
    public void getAccessToken_notFound() {
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID, TestConstants.DEFAULT_LOCALE)).willThrow(new RuntimeException());

        Optional<InternalAccessTokenResponse> result = underTest.getAccessToken(ACCESS_TOKEN_ID_STRING);

        assertThat(result).isEmpty();
    }
}