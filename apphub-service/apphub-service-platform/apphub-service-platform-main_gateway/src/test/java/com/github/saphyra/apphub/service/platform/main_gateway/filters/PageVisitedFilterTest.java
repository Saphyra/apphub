package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PageVisitedFilterTest {
    private static final String PATH = "/path";
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private EventGatewayApiClient eventClient;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private PageVisitedFilter underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Mono<Void> mono;

    @Mock
    private HttpCookie cookie;

    @Mock
    private ServerHttpRequest request;

    private final MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @Captor
    private ArgumentCaptor<SendEventRequest<PageVisitedEvent>> argumentCaptor;

    @Before
    public void setUp() {
        given(exchange.getRequest()).willReturn(request);
        given(request.getURI()).willReturn(URI.create(UrlFactory.create(1000, PATH)));

        given(filterChain.filter(exchange)).willReturn(mono);

        given(request.getCookies()).willReturn(cookies);
    }

    @Test
    public void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(FilterOrder.PAGE_VISITED_FILTER.getOrder());
    }

    @Test
    public void notWebPath() {
        given(uriUtils.isWebPath(PATH)).willReturn(false);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(eventClient);
    }

    @Test
    public void webPath_noAccessToken() {
        given(uriUtils.isWebPath(PATH)).willReturn(true);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verifyNoInteractions(eventClient);
    }

    @Test
    public void webPath() {
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(uriUtils.isWebPath(PATH)).willReturn(true);
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID_STRING);
        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        Mono<Void> result = underTest.filter(exchange, filterChain);

        assertThat(result).isEqualTo(mono);
        verify(eventClient).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<PageVisitedEvent> sendEventRequest = argumentCaptor.getValue();
        assertThat(sendEventRequest.getPayload().getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(sendEventRequest.getPayload().getPageUrl()).isEqualTo(PATH);
    }
}