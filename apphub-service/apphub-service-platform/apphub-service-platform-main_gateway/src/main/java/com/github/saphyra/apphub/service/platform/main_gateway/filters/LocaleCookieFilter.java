package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocaleCookieFilter implements GlobalFilter, Ordered {
    private final ApphubLocaleResolver localeResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> result = chain.filter(exchange);
        try {
            ServerHttpRequest request = exchange.getRequest();
            String locale = localeResolver.getLocale(request.getHeaders(), request.getCookies());
            log.debug("Locale selected for user: {}", locale);
            ResponseCookie cookie = ResponseCookie.from(Constants.LOCALE_COOKIE, locale)
                .httpOnly(false)
                .path("/")
                .maxAge(Integer.MAX_VALUE)
                .build();
            exchange.getResponse()
                .addCookie(cookie);
        } catch (Exception e) {
            log.error("Failed setting locale cookie", e);
            //TODO report error
        }
        return result;
    }

    @Override
    public int getOrder() {
        return FilterOrder.LOCALE_COOKIE_FILTER.getOrder();
    }
}
