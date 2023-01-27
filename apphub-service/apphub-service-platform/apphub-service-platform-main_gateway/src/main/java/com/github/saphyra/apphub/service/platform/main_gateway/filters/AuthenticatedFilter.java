package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.config.whitelist.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthenticationService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthenticatedFilter implements GlobalFilter, Ordered {
    private final UriUtils uriUtils;
    private final AntPathMatcher antPathMatcher;
    private final AuthenticationService authenticationService;
    private final WhiteListedEndpointProperties endpointProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String requestUri = uri.getPath();
        HttpMethod requestMethod = request.getMethod();

        if (uriUtils.isResourcePath(requestUri)) {
            log.debug("Resource path");
            return chain.filter(exchange);
        }

        if (isWhiteListedEndpoint(requestMethod, requestUri)) {
            log.debug("Whitelisted endpoint");
            return chain.filter(exchange);
        }

        return authenticationService.authenticate(exchange.getRequest())
            .handle(exchange, chain);
    }

    private boolean isWhiteListedEndpoint(HttpMethod requestMethod, String requestUri) {
        return endpointProperties.getWhiteListedEndpoints()
            .values()
            .stream()
            .filter(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPattern(), requestUri))
            .anyMatch(whiteListedEndpoint -> whiteListedEndpoint.getMethod().equalsIgnoreCase(requestMethod.name()));
    }

    @Override
    public int getOrder() {
        return FilterOrder.AUTHENTICATION_FILTER.getOrder();
    }
}
