package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.data.client.UserDataApiClient;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.util.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_COOKIE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class LocaleFilter extends ZuulFilter {
    private final AccessTokenQueryService accessTokenQueryService;
    private final CommonConfigProperties commonConfigProperties;
    private final CookieUtil cookieUtil;
    private final UserDataApiClient userDataApi;

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
    //TODO accept only supported locales
    //TODO unit test
    //TODO refactor
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String locale = null;

        Optional<UUID> userIdOptional = cookieUtil.getCookie(request, ACCESS_TOKEN_COOKIE)
            .flatMap(accessTokenQueryService::getAccessToken)
            .map(InternalAccessTokenResponse::getUserId);
        if (userIdOptional.isPresent()) {
            try {
                locale = userDataApi.getLanguage(userIdOptional.get(), commonConfigProperties.getDefaultLocale());
            } catch (Exception e) {
                log.warn("Error occurred during userLanguage query", e);
            }
        }

        if (isNull(locale)) {
            Optional<String> localeCookieOptional = cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)
                .filter(cookieValue -> !isBlank(cookieValue));
            if (localeCookieOptional.isPresent()) {
                locale = localeCookieOptional.get();
            }
        }

        if (isNull(locale)) {
            Optional<String> browserLanguageHeaderOptional = Optional.ofNullable(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER))
                .filter(headerValue -> !isBlank(headerValue));
            if (browserLanguageHeaderOptional.isPresent()) {
                locale = browserLanguageHeaderOptional.get();
            }
        }

        if (isNull(locale)) {
            locale = commonConfigProperties.getDefaultLocale();
        }

        requestContext.addZuulRequestHeader(Constants.LOCALE_HEADER, locale);
        return null;
    }
}
