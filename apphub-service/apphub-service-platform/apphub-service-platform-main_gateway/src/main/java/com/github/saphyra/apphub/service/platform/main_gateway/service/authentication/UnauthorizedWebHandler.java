package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UnauthorizedWebHandler implements AuthenticationResultHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        exchange.getResponse()
            .getHeaders()
            .add(HttpHeaders.LOCATION, Endpoints.INDEX_PAGE + "?redirect=" + exchange.getRequest().getPath());
        return Mono.empty();
    }
}
