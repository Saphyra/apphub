package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.UserSettingLocaleResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ChangeLocaleFilterTest {
    @Mock
    private ErrorReporterService errorReporterService;

    @SuppressWarnings("unused")
    @Spy
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Mock
    private UserSettingLocaleResolver userSettingLocaleResolver;

    @InjectMocks
    private ChangeLocaleFilter underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private MultiValueMap<String, HttpCookie> cookies;

    @Mock
    private Mono<Void> mono;

    @Test
    public void filter_changeLocaleEndpoint() {
        given(chain.filter(exchange)).willReturn(mono);
        given(exchange.getRequest()).willReturn(request);
        given(request.getCookies()).willReturn(cookies);
        given(request.getURI()).willReturn(URI.create(UserEndpoints.ACCOUNT_CHANGE_LANGUAGE));

        Mono<Void> result = underTest.filter(exchange, chain);

        assertThat(result).isEqualTo(mono);
        verify(userSettingLocaleResolver).invalidate(cookies);
        verifyNoInteractions(errorReporterService);
    }

    @Test
    public void filter_notChangeLocaleEndpoint() {
        given(chain.filter(exchange)).willReturn(mono);
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(SkyXploreDataEndpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        Mono<Void> result = underTest.filter(exchange, chain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(userSettingLocaleResolver);
        verifyNoInteractions(errorReporterService);
    }

    @Test
    public void filter_error() {
        given(chain.filter(exchange)).willReturn(mono);
        given(exchange.getRequest()).willReturn(request);
        RuntimeException exception = new RuntimeException("asd");
        given(request.getURI()).willThrow(exception);

        Mono<Void> result = underTest.filter(exchange, chain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(userSettingLocaleResolver);
        verify(errorReporterService).report(anyString(), eq(exception));
    }
}