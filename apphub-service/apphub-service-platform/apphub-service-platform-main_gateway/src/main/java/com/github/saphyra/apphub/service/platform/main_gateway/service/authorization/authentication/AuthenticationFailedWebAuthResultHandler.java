package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationFailedWebAuthResultHandler implements AuthResultHandler {
    private final String redirectUrl;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders()
            .add(HttpHeaders.LOCATION, redirectUrl);
        return Mono.empty();
    }
}
