package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class WsSessionUserIdProviderTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserAuthenticationClient userAuthenticationApi;

    @Mock
    private WsSessionLocaleProvider localeProvider;

    @Mock
    private WsSessionAccessTokenProvider accessTokenProvider;

    @Mock
    private WsSessionCookieParser cookieParser;

    @InjectMocks
    private WsSessionUserIdProvider underTest;

    @Mock
    private ServerHttpRequest request;

    @Test
    public void findUserId() {
        given(cookieParser.getCookies(request)).willReturn(new HashMap<>());
        given(accessTokenProvider.getAccessTokenId(any())).willReturn(ACCESS_TOKEN_ID);
        given(localeProvider.getLocale(any())).willReturn(LOCALE);
        given(userAuthenticationApi.getAccessTokenById(ACCESS_TOKEN_ID, LOCALE)).willReturn(InternalAccessTokenResponse.builder().userId(USER_ID).build());

        UUID result = underTest.findUserId(request);

        assertThat(result).isEqualTo(USER_ID);
    }
}