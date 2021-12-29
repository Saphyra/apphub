package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenCacheTest {
    private static final String LOCALE = "locale";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private UserAuthenticationClient authenticationClient;

    @InjectMocks
    private AccessTokenCache underTest;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void loadAndReturn() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
        given(authenticationClient.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willReturn(accessTokenResponse);

        assertThat(underTest.get(ACCESS_TOKEN_ID)).contains(accessTokenResponse);
        assertThat(underTest.get(ACCESS_TOKEN_ID)).contains(accessTokenResponse);

        new SleepService().sleep(3000);

        assertThat(underTest.get(ACCESS_TOKEN_ID)).contains(accessTokenResponse);

        verify(authenticationClient, times(2)).getAccessTokenById(ACCESS_TOKEN_ID, LOCALE);
    }
}