package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthResultHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
//TODO unit test
public class AuthorizationFailedWebHandler implements AuthResultHandler {
    private final String redirectUrl;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        exchange.getResponse()
            .getHeaders()
            .add(HttpHeaders.LOCATION, redirectUrl);
        return Mono.empty();
    }
}
