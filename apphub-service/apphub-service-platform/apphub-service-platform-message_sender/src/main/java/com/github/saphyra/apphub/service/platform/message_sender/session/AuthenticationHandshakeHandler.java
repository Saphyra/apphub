package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
@Slf4j
//TODO unit test
//TODO refactor - split
public class AuthenticationHandshakeHandler extends DefaultHandshakeHandler {
    private final UserAuthenticationApiClient userAuthenticationApi;
    private final UuidConverter uuidConverter;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        UUID userId = findUserId(request);
        log.info("{} is connected.", userId);
        return () -> uuidConverter.convertDomain(userId);
    }

    private UUID findUserId(ServerHttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        if (log.isDebugEnabled()) {
            log.debug("HttpHeaders: {}", httpHeaders);
        }

        List<String> cookieList = httpHeaders.get("Cookie");

        if (log.isDebugEnabled()) {
            log.debug("Cookies: {}", cookieList);
        }

        if (isNull(cookieList)) {
            throw new IllegalArgumentException("Cookies not found in headers");
        }

        if (cookieList.isEmpty()) {
            throw new IllegalArgumentException("Cookie list is empty.");
        }
        String cookieString = cookieList.get(0);
        String[] cookieArray = cookieString.split("; ");

        Map<String, Pair> pairs = Arrays.stream(cookieArray)
            .map(s -> {
                String[] sArray = s.split("=");
                return Pair.builder()
                    .key(sArray[0])
                    .value(sArray[1])
                    .build();
            })
            .collect(Collectors.toMap(Pair::getKey, Function.identity()));

        Pair accessTokenPair = pairs.get(Constants.ACCESS_TOKEN_COOKIE);
        if (isNull(accessTokenPair)) {
            throw new IllegalArgumentException("AccessToken cookie not found.");
        }
        String accessTokenIdString = accessTokenPair.getValue();

        Pair localePair = pairs.get(Constants.LOCALE_COOKIE);
        if (isNull(localePair)) {
            throw new IllegalArgumentException("Locale cookie not found.");
        }
        String locale = localePair.getValue();

        UUID accessTokenId = uuidConverter.convertEntity(accessTokenIdString);
        InternalAccessTokenResponse accessTokenResponse = userAuthenticationApi.getAccessTokenById(accessTokenId, locale);

        return accessTokenResponse.getUserId();
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static class Pair {
        private String key;
        private String value;
    }
}
