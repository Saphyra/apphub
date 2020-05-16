package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import static com.github.saphyra.apphub.lib.common_util.Constants.RESOURCE_PATH_PATTERN;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggingFilter extends ZuulFilter {
    private final AntPathMatcher antPathMatcher;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.REQUEST_LOGGING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        String requestUri = RequestContext.getCurrentContext().getRequest().getRequestURI();
        return !antPathMatcher.match(RESOURCE_PATH_PATTERN, requestUri);
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        log.info("Handling request {} - {}", requestContext.getRequest().getMethod(), requestContext.getRequest().getRequestURI());
        return null;
    }
}
