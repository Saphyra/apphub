package com.github.saphyra.apphub.service.platform.main_gateway.util;

import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.saphyra.apphub.lib.common_util.Constants.API_URI_PATTERN;
import static com.github.saphyra.apphub.lib.common_util.Constants.INDEX_PAGE_URI;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;

@RunWith(MockitoJUnitRunner.class)
public class ErrorResponseHandlerTest {
    private static final String RESPONSE_BODY = "response-body";
    private static final String STRINGIFIED_RESPONSE_BODY = "stringified-response-body";
    private static final String REQUEST_URI = "request-uri";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private ErrorResponseHandler underTest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void handleUnauthorized_rest() {
        given(requestContext.getRequest()).willReturn(request);
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(API_URI_PATTERN, REQUEST_URI)).willReturn(true);
        given(objectMapperWrapper.writeValueAsString(RESPONSE_BODY)).willReturn(STRINGIFIED_RESPONSE_BODY);

        underTest.handleUnauthorized(requestContext, RESPONSE_BODY);

        verify(requestContext).setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        verify(requestContext).setResponseBody(STRINGIFIED_RESPONSE_BODY);
        verify(requestContext).setSendZuulResponse(false);
        verify(requestContext).addZuulResponseHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    @Test
    public void handleBadRequest_rest() {
        given(requestContext.getRequest()).willReturn(request);
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(API_URI_PATTERN, REQUEST_URI)).willReturn(true);
        given(objectMapperWrapper.writeValueAsString(RESPONSE_BODY)).willReturn(STRINGIFIED_RESPONSE_BODY);

        underTest.handleBadRequest(requestContext, RESPONSE_BODY);

        verify(requestContext).setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
        verify(requestContext).setResponseBody(STRINGIFIED_RESPONSE_BODY);
        verify(requestContext).setSendZuulResponse(false);
        verify(requestContext).addZuulResponseHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    @Test
    public void handleUnauthorized_redirect() throws IOException {
        given(requestContext.getRequest()).willReturn(request);
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(API_URI_PATTERN, REQUEST_URI)).willReturn(false);
        given(requestContext.getResponse()).willReturn(response);

        underTest.handleUnauthorized(requestContext, RESPONSE_BODY);

        verify(requestContext).setSendZuulResponse(false);
        verify(requestContext).put(FORWARD_TO_KEY, INDEX_PAGE_URI);
        verify(requestContext).setResponseStatusCode(HttpStatus.TEMPORARY_REDIRECT.value());
        verify(response).sendRedirect(INDEX_PAGE_URI);
    }

    @Test
    public void handleUnauthorized_redirectionFailed() throws IOException {
        given(requestContext.getRequest()).willReturn(request);
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(API_URI_PATTERN, REQUEST_URI)).willReturn(false);
        given(requestContext.getResponse()).willReturn(response);
        doThrow(new IOException(RESPONSE_BODY)).when(response).sendRedirect(INDEX_PAGE_URI);
        given(objectMapperWrapper.writeValueAsString(RESPONSE_BODY)).willReturn(STRINGIFIED_RESPONSE_BODY);

        underTest.handleUnauthorized(requestContext, RESPONSE_BODY);

        verify(requestContext).setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        verify(requestContext).setResponseBody(STRINGIFIED_RESPONSE_BODY);
        verify(requestContext).setSendZuulResponse(false);
    }
}