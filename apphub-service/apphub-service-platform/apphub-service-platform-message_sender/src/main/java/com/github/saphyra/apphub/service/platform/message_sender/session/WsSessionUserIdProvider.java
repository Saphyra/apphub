package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class WsSessionUserIdProvider {
    private final UserAuthenticationApiClient userAuthenticationApi;
    private final WsSessionLocaleProvider localeProvider;
    private final WsSessionAccessTokenProvider accessTokenProvider;
    private final WsSessionCookieParser cookieParser;

    UUID findUserId(ServerHttpRequest request) {
        Map<String, String> cookies = cookieParser.getCookies(request);

        UUID accessTokenId = accessTokenProvider.getAccessTokenId(cookies);

        String locale = localeProvider.getLocale(cookies);

        InternalAccessTokenResponse accessTokenResponse = userAuthenticationApi.getAccessTokenById(accessTokenId, locale);
        return accessTokenResponse.getUserId();
    }
}
