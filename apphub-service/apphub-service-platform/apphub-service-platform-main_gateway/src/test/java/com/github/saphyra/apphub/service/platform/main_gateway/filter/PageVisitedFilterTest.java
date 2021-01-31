package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.CookieUtil;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.service.platform.main_gateway.filter.PageVisitedFilter.WEB_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PageVisitedFilterTest {
    private static final String URI = "uri";
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private RequestContextProvider requestContextProvider;

    @Mock
    private EventGatewayApiClient eventClient;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private PageVisitedFilter underTest;

    @Mock
    private HttpServletRequest request;

    @Captor
    ArgumentCaptor<SendEventRequest<PageVisitedEvent>> argumentCaptor;

    @Before
    public void setUp() {
        given(requestContextProvider.getCurrentHttpRequest()).willReturn(request);
    }

    @Test
    public void filterType() {
        assertThat(underTest.filterType()).isEqualTo(FilterConstants.PRE_TYPE);
    }

    @Test
    public void filterOrder() {
        assertThat(underTest.filterOrder()).isEqualTo(FilterOrder.PAGE_VISITED_FILTER.getOrder());
    }

    @Test
    public void shouldFilter_notPageUrl() {
        given(request.getRequestURI()).willReturn(URI);
        given(antPathMatcher.match(WEB_PATTERN, URI)).willReturn(false);

        boolean result = underTest.shouldFilter();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldFilter_accessTokenNotPresent() {
        given(request.getRequestURI()).willReturn(URI);
        given(antPathMatcher.match(WEB_PATTERN, URI)).willReturn(true);
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.empty());

        boolean result = underTest.shouldFilter();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldFilter() {
        given(request.getRequestURI()).willReturn(URI);
        given(antPathMatcher.match(WEB_PATTERN, URI)).willReturn(true);
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of("asd"));

        boolean result = underTest.shouldFilter();

        assertThat(result).isTrue();
    }

    @Test
    public void run() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(request.getRequestURI()).willReturn(URI);
        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.run();


        verify(eventClient).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<PageVisitedEvent> sendEventRequest = argumentCaptor.getValue();
        assertThat(sendEventRequest.getEventName()).isEqualTo(PageVisitedEvent.EVENT_NAME);
        assertThat(sendEventRequest.getPayload().getPageUrl()).isEqualTo(URI);
        assertThat(sendEventRequest.getPayload().getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
    }
}