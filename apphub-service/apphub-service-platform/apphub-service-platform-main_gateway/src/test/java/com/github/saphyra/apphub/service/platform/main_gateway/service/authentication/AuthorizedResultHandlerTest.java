package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizedResultHandlerTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";

    private AuthorizedResultHandler underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerWebExchange modifiedExchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpRequest modifiedRequest;

    @Mock
    private ServerHttpRequest.Builder requestBuilder;

    @Mock
    private ServerWebExchange.Builder exchangeBuilder;

    @Mock
    private Mono<Void> mono;

    @Test
    public void handle() {
        underTest = new AuthorizedResultHandler(ACCESS_TOKEN_HEADER);

        given(exchange.getRequest()).willReturn(request);
        given(request.mutate()).willReturn(requestBuilder);
        given(requestBuilder.build()).willReturn(modifiedRequest);
        given(requestBuilder.header(Constants.ACCESS_TOKEN_HEADER, ACCESS_TOKEN_HEADER)).willReturn(requestBuilder);

        given(exchange.mutate()).willReturn(exchangeBuilder);
        given(exchangeBuilder.request(modifiedRequest)).willReturn(exchangeBuilder);
        given(exchangeBuilder.build()).willReturn(modifiedExchange);

        given(filterChain.filter(modifiedExchange)).willReturn(mono);

        Mono<Void> result = underTest.handle(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verify(requestBuilder).header(Constants.ACCESS_TOKEN_HEADER, ACCESS_TOKEN_HEADER);
        verify(exchangeBuilder).request(modifiedRequest);
    }
}