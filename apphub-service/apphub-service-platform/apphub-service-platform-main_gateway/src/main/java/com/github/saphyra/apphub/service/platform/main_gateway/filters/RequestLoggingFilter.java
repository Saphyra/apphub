package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        String uri = request.getURI().getPath();
        log.info("Handling request: {} - {}", method, uri);
        Mono<Void> result = chain.filter(exchange);
        ServerHttpResponse response = exchange.getResponse();
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.info("Response status of {} - {}: {}", method, uri, response.getStatusCode());
        }
        return result;
    }

    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_LOGGING_FILTER.getOrder();
    }
}
