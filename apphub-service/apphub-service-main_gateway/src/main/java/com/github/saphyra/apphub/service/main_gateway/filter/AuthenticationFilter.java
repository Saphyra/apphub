package com.github.saphyra.apphub.service.main_gateway.filter;

import com.github.saphyra.apphub.api.user.authentication.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.config.whielist.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.main_gateway.FilterOrder;
import com.github.saphyra.util.CookieUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
//TODO refactor - split
public class AuthenticationFilter extends ZuulFilter {
    private static final String API_URI_PATTERN = "/api/**";
    private static final String INDEX_PAGE_URI = "/web";

    private final AntPathMatcher antPathMatcher;
    private final CookieUtil cookieUtil;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final UserAuthenticationApiClient authenticationApi;
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
        HttpServletRequest request = RequestContext.getCurrentContext()
            .getRequest();
        String requestUri = request
            .getRequestURI();
        String requestMethod = request.getMethod();
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
        Optional<String> accessTokenIdStringOptional = cookieUtil.getCookie(
            RequestContext.getCurrentContext().getRequest(),
            "access-token"
        );

        if (!accessTokenIdStringOptional.isPresent()) {
            handleUnauthorized();
            return null;
        }
        String accessTokenIdString = accessTokenIdStringOptional.get();
        Optional<UUID> accessTokenIdOptional = convertAccessTokenId(accessTokenIdString);
        if (!accessTokenIdOptional.isPresent()) {
            handleBadRequest();
            return null;
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = getAccessToken(accessTokenIdOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            handleUnauthorized();
            return null;
        }

        String accessTokenString = objectMapperWrapper.writeValueAsString(accessTokenResponseOptional.get());
        log.debug("Stringified accessToken: {}", accessTokenString);
        String encodedAccessToken = Arrays.toString(Base64.encodeBase64(accessTokenString.getBytes()));
        log.info("Enriching request with auth header: {}", encodedAccessToken);
        RequestContext.getCurrentContext().addZuulRequestHeader("access-token", encodedAccessToken);
        return null;
    }

    private Optional<UUID> convertAccessTokenId(String accessTokenIdString) {
        try {
            return Optional.of(UUID.fromString(accessTokenIdString));
        } catch (Throwable ex) {
            log.warn("Failed to parse accessToken: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<InternalAccessTokenResponse> getAccessToken(UUID accessTokenId) {
        try {
            return Optional.of(authenticationApi.getAccessTokenById(accessTokenId));
        } catch (Throwable e) {
            log.warn("Failed to query accessToken by accessTokenId {}: {}", accessTokenId, e.getMessage());
            return Optional.empty();
        }
    }

    private void handleUnauthorized() {
        if (isRestCall()) {
            sendRestErrorResponse(HttpStatus.UNAUTHORIZED, "");
        } else {
            redirectToIndex();
        }
    }

    private void handleBadRequest() {
        if (isRestCall()) {
            sendRestErrorResponse(HttpStatus.BAD_REQUEST, ""); //TODO add proper response
        } else {
            redirectToIndex();
        }
    }

    private boolean isRestCall() {
        return antPathMatcher.match(API_URI_PATTERN, RequestContext.getCurrentContext().getRequest().getRequestURI());
    }

    private void sendRestErrorResponse(HttpStatus status, String responseBody) {
        RequestContext.getCurrentContext().setResponseStatusCode(status.value());
        RequestContext.getCurrentContext().setResponseBody(responseBody);
        RequestContext.getCurrentContext().setSendZuulResponse(false);
    }

    private void redirectToIndex() {
        try {
            RequestContext context = RequestContext.getCurrentContext();
            context.setSendZuulResponse(false);
            context.put(FORWARD_TO_KEY, INDEX_PAGE_URI);
            context.setResponseStatusCode(HttpStatus.TEMPORARY_REDIRECT.value());
            context.getResponse().sendRedirect(INDEX_PAGE_URI);
        } catch (IOException e) {
            sendRestErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
