package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenCache;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenIdConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutFilter implements GlobalFilter, Ordered {
    private static final Map<String, HttpMethod> ENDPOINT_MAP = new HashMap<>() {{
        put(Endpoints.LOGOUT, HttpMethod.POST);
        put(Endpoints.ACCOUNT_DELETE_ACCOUNT, HttpMethod.DELETE);
    }};

    private final AntPathMatcher antPathMatcher;
    private final ErrorReporterService errorReporterService;
    private final AccessTokenCache accessTokenCache;
    private final AccessTokenIdConverter accessTokenIdConverter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> result = chain.filter(exchange);
        try {
            ServerHttpRequest request = exchange.getRequest();
            if (shouldRun(request.getURI().getPath(), request.getMethod())) {
                Optional<UUID> accessTokenId = Optional.ofNullable(request.getCookies()
                    .getFirst(Constants.ACCESS_TOKEN_COOKIE))
                    .map(HttpCookie::getValue)
                    .flatMap(accessTokenIdConverter::convertAccessTokenId);

                if (accessTokenId.isEmpty()) {
                    accessTokenId = Optional.ofNullable(request.getHeaders().getFirst(Constants.ACCESS_TOKEN_HEADER))
                        .flatMap(accessTokenIdConverter::convertAccessTokenId);
                }

                accessTokenId.ifPresent(accessTokenCache::invalidate);
            }
        } catch (Exception e) {
            log.error("Failed clearing accessToken cache", e);
            errorReporterService.report("Failed clearing accessToken cache", e);
        }
        return result;
    }

    private boolean shouldRun(String path, HttpMethod method) {
        return ENDPOINT_MAP.entrySet()
            .stream()
            .filter(entry -> method == entry.getValue())
            .anyMatch(entry -> {
                String key = entry.getKey();
                log.info("key: {}, path: {}", key, path);
                return antPathMatcher.match(key, path);
            });
    }

    @Override
    public int getOrder() {
        return FilterOrder.LOGOUT_FILTER.getOrder();
    }
}
