package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocaleFilter implements GlobalFilter, Ordered {
    private final UriUtils uriUtils;
    private final ApphubLocaleResolver apphubLocaleResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUri = exchange.getRequest()
            .getURI()
            .getPath();

        log.debug("Checking if locale present for requestUri {}", requestUri);

        if (!uriUtils.isResourcePath(requestUri)) {
            ServerHttpRequest request = exchange.getRequest();
            String locale = apphubLocaleResolver.getLocale(request.getHeaders(), request.getCookies());
            log.debug("Adding locale header {} to request {} - {}", locale, request.getMethod(), request.getPath());
            ServerHttpRequest mutatedRequest = request.mutate()
                .header(Constants.LOCALE_HEADER, locale)
                .build();
            exchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.LOCALE_FILTER.getOrder();
    }
}
