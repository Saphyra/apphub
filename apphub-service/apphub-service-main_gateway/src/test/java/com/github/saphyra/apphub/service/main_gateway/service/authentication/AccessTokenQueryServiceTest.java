package com.github.saphyra.apphub.service.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.authentication.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
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

    @Mock
    private UserAuthenticationApiClient authenticationApi;

    @InjectMocks
    private AccessTokenQueryService underTest;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void getAccessToken() {
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID)).willReturn(accessTokenResponse);

        Optional<InternalAccessTokenResponse> result = underTest.getAccessToken(ACCESS_TOKEN_ID);

        assertThat(result).contains(accessTokenResponse);
    }

    @Test
    public void getAccessToken_notFound() {
        given(authenticationApi.getAccessTokenById(ACCESS_TOKEN_ID)).willThrow(new RuntimeException());

        Optional<InternalAccessTokenResponse> result = underTest.getAccessToken(ACCESS_TOKEN_ID);

        assertThat(result).isEmpty();
    }
}