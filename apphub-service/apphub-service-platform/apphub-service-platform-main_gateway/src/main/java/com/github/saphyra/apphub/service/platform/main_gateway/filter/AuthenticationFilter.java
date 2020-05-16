package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.config.whielist.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthenticationService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;
import static com.github.saphyra.apphub.lib.common_util.Constants.RESOURCE_PATH_PATTERN;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends ZuulFilter {
    private final AntPathMatcher antPathMatcher;
    private final AuthenticationService authenticationService;
    private final WhiteListedEndpointProperties endpointProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.AUTHENTICATION_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        currentContext.addZuulRequestHeader(ACCESS_TOKEN_HEADER, "");
        HttpServletRequest request = currentContext
            .getRequest();
        String requestUri = request
            .getRequestURI();
        String requestMethod = request.getMethod();
        if (antPathMatcher.match(RESOURCE_PATH_PATTERN, requestUri)) {
            return false;
        }
        boolean isWhiteListedUrl = endpointProperties.getWhiteListedEndpoints()
            .values()
            .stream()
            .filter(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPath(), requestUri))
            .anyMatch(whiteListedEndpoint -> whiteListedEndpoint.getMethod().equalsIgnoreCase(requestMethod));

        log.info("{} - {} is whiteListed: {}", requestMethod, requestUri, isWhiteListedUrl);
        return !isWhiteListedUrl;
    }

    @Override
    public Object run() {
        authenticationService.authenticate(RequestContext.getCurrentContext());
        return null;
    }
}
