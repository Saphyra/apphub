package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Data
public class ErrorRestHandler implements AuthResultHandler {
    private final ErrorResponseWrapper errorResponse;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorResponse.getStatus());
        response.getHeaders()
            .add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        byte[] payload = objectMapperWrapper.writeValueAsString(errorResponse.getErrorResponse())
            .getBytes();
        DataBuffer dataBuffer = response
            .bufferFactory()
            .wrap(payload);
        return response.writeAndFlushWith(Mono.just(Mono.just(dataBuffer)));
    }
}
