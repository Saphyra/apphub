package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import static com.github.saphyra.apphub.lib.common_util.Constants.RESOURCE_PATH_PATTERN;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocaleFilter extends ZuulFilter {

    private final AntPathMatcher antPathMatcher;
    private final ApphubLocaleResolver apphubLocaleResolver;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.LOCALE_FILTER.getOrder();
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

        String locale = apphubLocaleResolver.getLocale(request);

        requestContext.addZuulRequestHeader(Constants.LOCALE_HEADER, locale);
        return null;
    }
}
