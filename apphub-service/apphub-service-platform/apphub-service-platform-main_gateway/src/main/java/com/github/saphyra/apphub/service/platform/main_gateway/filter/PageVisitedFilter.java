package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.CookieUtil;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO logging
public class PageVisitedFilter extends ZuulFilter {
    private static final String WEB_PATTERN = "/web/**";

    private final AntPathMatcher antPathMatcher;
    private final RequestContextProvider requestContextProvider;
    private final EventGatewayApiClient eventClient;
    private final CookieUtil cookieUtil;
    private final UuidConverter uuidConverter;
    private final CommonConfigProperties commonConfigProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.PAGE_VISITED_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = requestContextProvider.getCurrentHttpRequest();
        String url = request.getRequestURI();
        boolean isPageUrl = antPathMatcher.match(WEB_PATTERN, url);
        boolean accessTokenPresent = cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)
            .isPresent();
        return isPageUrl && accessTokenPresent;
    }

    @Override
    public Object run() {
        HttpServletRequest request = requestContextProvider.getCurrentHttpRequest();
        String accessTokenId = cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)
            .orElseThrow(() -> new IllegalStateException("AccessToken Cookie not found"));

        PageVisitedEvent event = PageVisitedEvent.builder()
            .accessTokenId(uuidConverter.convertEntity(accessTokenId))
            .pageUrl(request.getRequestURI())
            .build();
        SendEventRequest<PageVisitedEvent> sendEventRequest = SendEventRequest.<PageVisitedEvent>builder()
            .eventName(PageVisitedEvent.EVENT_NAME)
            .payload(event)
            .build();
        eventClient.sendEvent(
            sendEventRequest,
            commonConfigProperties.getDefaultLocale()
        );

        return null;
    }
}
