package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.config.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthorizationService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import com.github.saphyra.apphub.test.rest_assured.UrlFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthorizationFilterTest {
    private static final String PATH = "/path";
    private static final String METHOD = "POST";
    private static final String PATTERN = "pattern";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private WhiteListedEndpointProperties endpointProperties;

    @InjectMocks
    private AuthorizationFilter underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Mono<Void> mono;

    @Mock
    private AuthResultHandler authResultHandler;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(FilterOrder.AUTHORIZATION_FILTER.getOrder());
    }

    @Test
    void resourcePath() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(filterChain.filter(exchange)).willReturn(mono);

        given(uriUtils.isResourcePath(PATH)).willReturn(true);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(authorizationService);
    }

    @Test
    void whiteListedEndpoint() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(filterChain.filter(exchange)).willReturn(mono);
        given(endpointProperties.getWhiteListedEndpoints()).willReturn(CollectionUtils.singleValueMap("asd", new WhiteListedEndpoint(PATTERN, METHOD)));
        given(antPathMatcher.match(PATTERN, PATH)).willReturn(true);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(authorizationService);
    }

    @Test
    void handleAuthorization() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(endpointProperties.getWhiteListedEndpoints()).willReturn(CollectionUtils.singleValueMap("asd", new WhiteListedEndpoint(PATTERN, METHOD)));
        given(antPathMatcher.match(PATTERN, PATH)).willReturn(false);

        given(authorizationService.authorize(request)).willReturn(Mono.just(authResultHandler));
        given(authResultHandler.handle(exchange, filterChain)).willReturn(Mono.empty());

        StepVerifier.create(underTest.filter(exchange, filterChain))
            .expectNextCount(0)
            .verifyComplete();
    }
}