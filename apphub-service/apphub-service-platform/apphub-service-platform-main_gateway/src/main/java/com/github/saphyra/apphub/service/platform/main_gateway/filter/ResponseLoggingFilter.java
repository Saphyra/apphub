package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.saphyra.apphub.lib.common_util.Constants.RESOURCE_PATH_PATTERN;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseLoggingFilter extends ZuulFilter {
    private final AntPathMatcher antPathMatcher;

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.RESPONSE_LOGGING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        String requestUri = RequestContext.getCurrentContext().getRequest().getRequestURI();
        return !antPathMatcher.match(RESOURCE_PATH_PATTERN, requestUri);
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();

        log.info("Response of request {} - {}: HttpStatus: {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        return null;
    }
}
