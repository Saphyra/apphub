package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class AccessTokenExpirationUpdateService {
    private final AntPathMatcher antPathMatcher;
    private final CommonConfigProperties commonConfigProperties;
    private final EventGatewayApiClient eventGatewayApi;
    private final NonSessionExtendingUriProperties nonSessionExtendingUriProperties;

    void updateExpiration(HttpMethod requestMethod, String requestUri, UUID accessTokenId) {
        boolean isNonSessionExtendingEndpoint = nonSessionExtendingUriProperties.getNonSessionExtendingUris()
            .stream()
            .filter(nonSessionExtendingUri -> nonSessionExtendingUri.getMethod().equalsIgnoreCase(requestMethod.name()))
            .anyMatch(nonSessionExtendingUri -> antPathMatcher.match(nonSessionExtendingUri.getPattern(), requestUri));

        if (isNonSessionExtendingEndpoint) {
            log.debug("{} - {} is a non-session-extending uri.", requestMethod, requestUri);
            return;
        }

        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(RefreshAccessTokenExpirationEvent.EVENT_NAME)
                .payload(new RefreshAccessTokenExpirationEvent(accessTokenId))
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }
}
