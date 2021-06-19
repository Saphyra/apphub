package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocaleCookieFilterTest {
    private static final String LOCALE = "locale";

    @Mock
    private ApphubLocaleResolver apphubLocaleResolver;

    @InjectMocks
    private LocaleCookieFilter underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Mono<Void> mono;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private HttpCookie cookie;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Test
    public void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(FilterOrder.LOCALE_COOKIE_FILTER.getOrder());
    }

    @Test
    public void filter() {
        given(filterChain.filter(exchange)).willReturn(mono);
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        cookies.put("asd", Arrays.asList(cookie));
        given(exchange.getRequest()).willReturn(request);
        given(exchange.getResponse()).willReturn(response);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(request.getCookies()).willReturn(cookies);

        given(apphubLocaleResolver.getLocale(httpHeaders, cookies)).willReturn(LOCALE);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        ArgumentCaptor<ResponseCookie> argumentCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(response).addCookie(argumentCaptor.capture());

        ResponseCookie responseCookie = argumentCaptor.getValue();
        assertThat(responseCookie.isHttpOnly()).isFalse();
        assertThat(responseCookie.getPath()).isEqualTo("/");
        assertThat(responseCookie.getMaxAge()).isEqualTo(Duration.ofSeconds(Integer.MAX_VALUE));
    }
}