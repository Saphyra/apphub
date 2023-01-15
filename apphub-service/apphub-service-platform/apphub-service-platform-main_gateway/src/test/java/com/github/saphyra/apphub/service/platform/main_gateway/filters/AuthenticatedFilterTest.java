package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.whitelist.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthenticationService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedFilterTest {
    private static final String PATH = "/path";
    private static final String METHOD = "method";
    private static final String PATTERN = "pattern";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private WhiteListedEndpointProperties endpointProperties;

    @InjectMocks
    private AuthenticatedFilter underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Mono<Void> mono;

    @BeforeEach
    public void setUp() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));
        given(request.getMethodValue()).willReturn(METHOD);
        given(filterChain.filter(exchange)).willReturn(mono);
    }

    @Test
    public void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(FilterOrder.AUTHENTICATION_FILTER.getOrder());
    }

    @Test
    public void resourcePath() {
        given(uriUtils.isResourcePath(PATH)).willReturn(true);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(authenticationService);
    }

    @Test
    public void whiteListedEndpoint() {
        given(endpointProperties.getWhiteListedEndpoints()).willReturn(CollectionUtils.singleValueMap("asd", new WhiteListedEndpoint(PATTERN, METHOD)));
        given(antPathMatcher.match(PATTERN, PATH)).willReturn(true);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(authenticationService);
    }

    @Test
    public void authenticate() {
        given(authenticationService.authenticate(request)).willReturn((exchange1, filterChain1) -> mono);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
    }
}