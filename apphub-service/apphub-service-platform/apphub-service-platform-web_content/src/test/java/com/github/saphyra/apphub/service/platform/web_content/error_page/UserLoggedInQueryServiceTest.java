package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserLoggedInQueryServiceTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private UserAuthenticationClient authenticationClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private UserLoggedInQueryService underTest;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void nullAccessTokenId() {
        assertThat(underTest.isUserLoggedIn(null)).isFalse();
    }

    @Test
    public void userLoggedIn() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(authenticationClient.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willReturn(accessTokenResponse);

        assertThat(underTest.isUserLoggedIn(ACCESS_TOKEN_ID)).isTrue();
    }

    @Test
    public void accessTokenQueryThrowsError() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(authenticationClient.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willThrow(new RuntimeException());

        assertThat(underTest.isUserLoggedIn(ACCESS_TOKEN_ID)).isFalse();
    }
}