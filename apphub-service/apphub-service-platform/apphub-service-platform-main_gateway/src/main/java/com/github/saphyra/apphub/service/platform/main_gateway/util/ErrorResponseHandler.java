package com.github.saphyra.apphub.service.platform.main_gateway.util;

import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

import static com.github.saphyra.apphub.lib.common_util.Constants.API_URI_PATTERN;
import static com.github.saphyra.apphub.lib.common_util.Constants.INDEX_PAGE_URI;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;

@Component
@RequiredArgsConstructor
public class ErrorResponseHandler {
    private final AntPathMatcher antPathMatcher;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void sendErrorResponse(RequestContext requestContext, ErrorResponseWrapper errorResponseWrapper) {
        handleRequest(requestContext, errorResponseWrapper.getStatus(), errorResponseWrapper.getErrorResponse());
    }

    private void handleRequest(RequestContext requestContext, HttpStatus status, Object responseBody) {
        if (isRestCall(requestContext.getRequest().getRequestURI())) {
            sendRestErrorResponse(requestContext, status, responseBody);
        } else {
            redirectToIndex(requestContext);
        }
    }

    private boolean isRestCall(String requestUri) {
        return antPathMatcher.match(API_URI_PATTERN, requestUri);
    }

    private void sendRestErrorResponse(RequestContext requestContext, HttpStatus status, Object responseBody) {
        requestContext.addZuulResponseHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        requestContext.setResponseStatusCode(status.value());
        requestContext.setResponseBody(objectMapperWrapper.writeValueAsString(responseBody));
        requestContext.setSendZuulResponse(false);
    }

    private void redirectToIndex(RequestContext requestContext) {
        try {
            requestContext.getResponse().sendRedirect(INDEX_PAGE_URI);
            requestContext.setSendZuulResponse(false);
            requestContext.put(FORWARD_TO_KEY, INDEX_PAGE_URI);
            requestContext.setResponseStatusCode(HttpStatus.TEMPORARY_REDIRECT.value());
        } catch (IOException e) {
            sendRestErrorResponse(requestContext, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
