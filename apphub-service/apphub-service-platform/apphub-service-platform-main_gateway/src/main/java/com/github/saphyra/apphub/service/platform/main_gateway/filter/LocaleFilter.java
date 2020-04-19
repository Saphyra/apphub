package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
public class LocaleFilter extends ZuulFilter {
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
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.addZuulRequestHeader(Constants.LOCALE_HEADER, "hu"); //TODO settable locale
        return null;
    }
}
