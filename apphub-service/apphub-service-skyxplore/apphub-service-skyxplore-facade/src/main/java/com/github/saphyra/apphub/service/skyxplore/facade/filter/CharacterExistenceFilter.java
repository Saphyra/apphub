package com.github.saphyra.apphub.service.skyxplore.facade.filter;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.skyxplore.facade.config.CharacterExistenceWhitelistConfiguration;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CharacterExistenceFilter extends ZuulFilter {
    private final SkyXploreCharacterDataApiClient characterApi;
    private final RequestContextProvider requestContextProvider;
    private final CharacterExistenceWhitelistConfiguration configuration;
    private final AntPathMatcher antPathMatcher;
    private final AccessTokenProvider accessTokenProvider;
    private final LocaleProvider localeProvider;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = requestContextProvider.getCurrentHttpRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (antPathMatcher.match(Constants.RESOURCE_PATH_PATTERN, uri)) {
            log.debug("Resource request");
            return false;
        }
        boolean isWhiteListed = configuration.getWhiteListedEndpoints()
            .stream()
            .anyMatch(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPath(), uri) && whiteListedEndpoint.getMethod().equalsIgnoreCase(method));
        if (isWhiteListed) {
            return false;
        }

        return !characterApi.doesCharacterExistForUser(accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    @SneakyThrows
    @Override
    public Object run() {
        String uri = requestContextProvider.getCurrentHttpRequest().getRequestURI();
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext
            .getResponse();
        if (antPathMatcher.match(Constants.API_URI_PATTERN, uri)) {
            requestContext.addZuulResponseHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            requestContext.setResponseStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            requestContext.setSendZuulResponse(false);
        } else {
            response.sendRedirect(Endpoints.SKYXPLORE_CHARACTER_PAGE);
        }

        return null;
    }
}
