package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PageVisitedFilter implements GlobalFilter, Ordered {
    private final UriUtils uriUtils;
    private final EventGatewayApiClient eventClient;
    private final UuidConverter uuidConverter;
    private final CommonConfigProperties commonConfigProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            String requestUri = request.getURI().getPath();
            if (uriUtils.isWebPath(requestUri)) {
                getAccessTokenId(request.getCookies())
                    .ifPresent(accessTokenID -> sendPageVisitedEvent(accessTokenID, requestUri));
            }
        } catch (Exception e) {
            log.error("Failed updating last visited page.", e);
        }

        return chain.filter(exchange);
    }

    private void sendPageVisitedEvent(String accessTokenId, String requestUri) {
        PageVisitedEvent event = PageVisitedEvent.builder()
            .accessTokenId(uuidConverter.convertEntity(accessTokenId))
            .pageUrl(requestUri)
            .build();
        SendEventRequest<PageVisitedEvent> sendEventRequest = SendEventRequest.<PageVisitedEvent>builder()
            .eventName(PageVisitedEvent.EVENT_NAME)
            .payload(event)
            .build();
        eventClient.sendEvent(
            sendEventRequest,
            commonConfigProperties.getDefaultLocale()
        );
    }

    private Optional<String> getAccessTokenId(MultiValueMap<String, HttpCookie> cookies) {
        return Optional.ofNullable(cookies.getFirst(Constants.ACCESS_TOKEN_COOKIE))
            .map(HttpCookie::getValue);
    }

    @Override
    public int getOrder() {
        return FilterOrder.PAGE_VISITED_FILTER.getOrder();
    }
}
