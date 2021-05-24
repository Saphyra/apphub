package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_util.Constants;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Data
public class AuthorizedResultHandler implements AuthenticationResultHandler {
    private final String accessTokenHeader;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        ServerHttpRequest newRequest = exchange.getRequest()
            .mutate()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeader)
            .build();
        ServerWebExchange newExchange = exchange.mutate()
            .request(newRequest)
            .build();
        return filterChain.filter(newExchange);
    }
}
