package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class LocaleFilterTest {
    private static final String PATH = "/path";
    private static final String LOCALE = "locale";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private ApphubLocaleResolver apphubLocaleResolver;

    @InjectMocks
    private LocaleFilter underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Mono<Void> mono;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private HttpCookie cookie;

    @Mock
    private ServerWebExchange modifiedExchange;

    @Mock
    private ServerWebExchange.Builder exchangeBuilder;

    @Mock
    private ServerHttpRequest.Builder requestBuilder;

    @Mock
    private ServerHttpRequest modifiedRequest;

    private final MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @BeforeEach
    public void setUp() {
        cookies.put("asd", Arrays.asList(cookie));
    }

    @Test
    public void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(FilterOrder.LOCALE_FILTER.getOrder());
    }

    @Test
    public void filter_resourcePath() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(filterChain.filter(exchange)).willReturn(mono);
        given(uriUtils.isResourcePath(PATH)).willReturn(true);
        given(filterChain.filter(exchange)).willReturn(mono);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);

        verifyNoInteractions(apphubLocaleResolver);
    }

    @Test
    public void filter() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(request.getHeaders()).willReturn(httpHeaders);
        given(request.getCookies()).willReturn(cookies);
        given(uriUtils.isResourcePath(PATH)).willReturn(false);
        given(apphubLocaleResolver.getLocale(httpHeaders, cookies)).willReturn(LOCALE);
        given(exchange.mutate()).willReturn(exchangeBuilder);
        given(exchangeBuilder.build()).willReturn(modifiedExchange);
        given(filterChain.filter(modifiedExchange)).willReturn(mono);
        given(request.mutate()).willReturn(requestBuilder);
        given(requestBuilder.build()).willReturn(modifiedRequest);
        given(requestBuilder.header(Constants.LOCALE_HEADER, LOCALE)).willReturn(requestBuilder);
        given(exchangeBuilder.request(modifiedRequest)).willReturn(exchangeBuilder);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
    }
}