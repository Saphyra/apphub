package com.github.saphyra.apphub.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        URI uri = request.getURI();
        log.info("Handling request: {} - {}", method, uri);
        Mono<Void> result = chain.filter(exchange);
        ServerHttpResponse response = exchange.getResponse();
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.info("Response status of {} - {}: {}", method, uri, response.getStatusCode());
        }
        return result;
    }
}
