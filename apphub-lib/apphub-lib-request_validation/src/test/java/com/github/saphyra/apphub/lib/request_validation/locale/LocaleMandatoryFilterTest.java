package com.github.saphyra.apphub.lib.request_validation.locale;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LocaleMandatoryFilterTest {
    private static final String RESPONSE_BODY = "response-body";
    private static final String REQUEST_URI = "request-uri";
    private static final String WHITELISTED_PATTERN = "whitelisted-pattern";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LocaleMandatoryFilterConfiguration localeMandatoryFilterConfiguration;

    @InjectMocks
    private LocaleMandatoryFilter underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    public void localePresent() throws ServletException, IOException {
        given(localeProvider.getLocale(request)).willReturn(Optional.of(TestConstants.DEFAULT_LOCALE));

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void localeNotPresent() throws ServletException, IOException {
        given(localeProvider.getLocale(request)).willReturn(Optional.empty());
        given(response.getWriter()).willReturn(printWriter);
        given(commonConfigProperties.getDefaultLocale()).willReturn(TestConstants.DEFAULT_LOCALE);
        given(errorResponseFactory.create(TestConstants.DEFAULT_LOCALE, HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND)).willReturn(errorResponseWrapper);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_REQUEST);
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(objectMapperWrapper.writeValueAsString(errorResponse)).willReturn(RESPONSE_BODY);
        given(localeMandatoryFilterConfiguration.getWhiteListedEndpoints()).willReturn(Collections.emptyList());

        underTest.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(printWriter).write(RESPONSE_BODY);
        verify(printWriter).flush();
        verify(printWriter).close();
    }

    @Test
    public void whiteListedEndpoint() throws ServletException, IOException {
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(request.getMethod()).willReturn(HttpMethod.POST.name());

        WhiteListedEndpoint whiteListedEndpoint = WhiteListedEndpoint.builder()
            .path(WHITELISTED_PATTERN)
            .method(HttpMethod.POST.name())
            .build();
        given(localeMandatoryFilterConfiguration.getWhiteListedEndpoints()).willReturn(Arrays.asList(whiteListedEndpoint));
        given(antPathMatcher.match(WHITELISTED_PATTERN, REQUEST_URI)).willReturn(true);

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}