package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthenticationFailedWebHandler implements AuthResultHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        exchange.getResponse()
            .getHeaders()
            .add(HttpHeaders.LOCATION, GenericEndpoints.INDEX_PAGE + "?redirect=" + exchange.getRequest().getPath());
        return Mono.empty();
    }
}
