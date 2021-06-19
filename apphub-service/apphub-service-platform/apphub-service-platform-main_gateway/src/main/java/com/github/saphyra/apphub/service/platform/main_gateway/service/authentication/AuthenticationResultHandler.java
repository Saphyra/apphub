package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationResultHandler {
    Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain);
}
