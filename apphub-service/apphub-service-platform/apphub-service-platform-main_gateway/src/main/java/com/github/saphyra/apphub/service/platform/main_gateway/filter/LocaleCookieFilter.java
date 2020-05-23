package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@Component
@RequiredArgsConstructor
public class LocaleCookieFilter extends ZuulFilter {
    private final ApphubLocaleResolver localeResolver;

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.LOCALE_COOKIE_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        Cookie cookie = new Cookie(Constants.LOCALE_COOKIE, localeResolver.getLocale(request));
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);

        return null;
    }
}
