package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class WsSessionUserIdProvider {
    private final UserAuthenticationClient userAuthenticationApi;
    private final WsSessionAccessTokenProvider accessTokenProvider;
    private final WsSessionCookieParser cookieParser;
    private final CommonConfigProperties commonConfigProperties;

    UUID findUserId(ServerHttpRequest request) {
        Map<String, String> cookies = cookieParser.getCookies(request);

        UUID accessTokenId = accessTokenProvider.getAccessTokenId(cookies);

        InternalAccessTokenResponse accessTokenResponse = userAuthenticationApi.getAccessTokenById(accessTokenId, commonConfigProperties.getDefaultLocale());
        return accessTokenResponse.getUserId();
    }
}
