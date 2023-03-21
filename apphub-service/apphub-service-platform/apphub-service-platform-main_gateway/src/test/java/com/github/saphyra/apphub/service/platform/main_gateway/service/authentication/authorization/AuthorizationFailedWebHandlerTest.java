package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizationFailedWebHandlerTest {
    private static final String REDIRECT_URL = "redirect-url";

    private final AuthorizationFailedWebHandler underTest = new AuthorizationFailedWebHandler(REDIRECT_URL);

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain gatewayFilterChain;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    @Test
    void handle() {
        given(exchange.getResponse()).willReturn(response);
        given(response.getHeaders()).willReturn(headers);

        Mono<Void> result = underTest.handle(exchange, gatewayFilterChain);

        assertThat(result).isEqualTo(Mono.empty());

        verify(response).setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        verify(headers).add(HttpHeaders.LOCATION, REDIRECT_URL);
    }
}