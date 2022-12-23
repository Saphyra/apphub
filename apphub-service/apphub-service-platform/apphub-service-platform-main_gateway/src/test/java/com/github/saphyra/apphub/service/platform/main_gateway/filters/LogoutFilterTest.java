package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenCache;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenIdConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LogoutFilterTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Spy
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private AccessTokenCache accessTokenCache;

    @Mock
    private AccessTokenIdConverter accessTokenIdConverter;

    @InjectMocks
    private LogoutFilter underTest;

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

    @Test
    public void shouldNotRun() throws URISyntaxException {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(new URI(Endpoints.USER_DATA_ROLES_FOR_ALL_RESTRICTED));
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(filterChain.filter(exchange)).willReturn(mono);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);

        verifyNoInteractions(accessTokenCache);
    }

    @Test
    public void invalidateAccessToken() throws URISyntaxException {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(new URI(Endpoints.LOGOUT));
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(filterChain.filter(exchange)).willReturn(mono);
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(Constants.ACCESS_TOKEN_HEADER)).willReturn(ACCESS_TOKEN_ID_STRING);
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);

        verify(accessTokenCache).invalidate(ACCESS_TOKEN_ID);
    }
}