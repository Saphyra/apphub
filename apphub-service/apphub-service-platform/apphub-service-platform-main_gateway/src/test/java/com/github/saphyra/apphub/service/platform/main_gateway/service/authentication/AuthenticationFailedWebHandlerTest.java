package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFailedWebHandlerTest {
    @InjectMocks
    private AuthenticationFailedWebHandler authenticationFailedWebHandler;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private RequestPath requestPath;

    @Test
    public void handle() {
        given(exchange.getResponse()).willReturn(response);
        given(exchange.getRequest()).willReturn(request);
        given(request.getPath()).willReturn(requestPath);

        given(response.getHeaders()).willReturn(httpHeaders);

        Mono<Void> result = authenticationFailedWebHandler.handle(exchange, filterChain);

        assertThat(result).isEqualTo(Mono.empty());

        verify(response).setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        verify(httpHeaders).add(HttpHeaders.LOCATION, GenericEndpoints.INDEX_PAGE + "?redirect=" + requestPath);
    }
}