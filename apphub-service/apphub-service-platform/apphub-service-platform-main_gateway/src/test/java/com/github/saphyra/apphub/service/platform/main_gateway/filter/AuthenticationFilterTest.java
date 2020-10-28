package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.config.whitelist.WhiteListedEndpointProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthenticationService;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;
import static com.github.saphyra.apphub.lib.common_util.Constants.RESOURCE_PATH_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest {
    private static final String WHITE_LIST_PATTERN = "white-list-pattern";
    private static final String WHITE_LISTED_ENDPOINT = "white-listed-endpoint";
    private static final String WHITE_LISTED_METHOD = "white-listed-method";
    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private WhiteListedEndpointProperties endpointProperties;

    @InjectMocks
    private AuthenticationFilter underTest;

    @Mock
    private WhiteListedEndpoint whiteListedEndpoint;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        Map<String, WhiteListedEndpoint> endpointMap = new HashMap<>();
        endpointMap.put("ep", whiteListedEndpoint);
        given(endpointProperties.getWhiteListedEndpoints()).willReturn(endpointMap);
        given(antPathMatcher.match(WHITE_LIST_PATTERN, WHITE_LISTED_ENDPOINT)).willReturn(true);

        RequestContext.testSetCurrentContext(requestContext);

        given(whiteListedEndpoint.getMethod()).willReturn(WHITE_LISTED_METHOD);
        given(whiteListedEndpoint.getPath()).willReturn(WHITE_LIST_PATTERN);

        given(requestContext.getRequest()).willReturn(request);
    }

    @Test
    public void shouldFilter_resourcePath(){
        given(request.getRequestURI()).willReturn(WHITE_LISTED_ENDPOINT);
        given(antPathMatcher.match(RESOURCE_PATH_PATTERN, WHITE_LISTED_ENDPOINT)).willReturn(true);

        boolean result = underTest.shouldFilter();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldFilter_whiteListedEndpoint() {
        given(request.getRequestURI()).willReturn(WHITE_LISTED_ENDPOINT);
        given(request.getMethod()).willReturn(WHITE_LISTED_METHOD);

        boolean result = underTest.shouldFilter();

        assertThat(result).isFalse();
        verify(requestContext).addZuulRequestHeader(ACCESS_TOKEN_HEADER, "");
    }

    @Test
    public void shouldFilter_blackListedUri() {
        given(request.getRequestURI()).willReturn("not-white-listed-uri");
        given(request.getMethod()).willReturn(WHITE_LISTED_METHOD);

        boolean result = underTest.shouldFilter();

        assertThat(result).isTrue();
        verify(requestContext).addZuulRequestHeader(ACCESS_TOKEN_HEADER, "");
    }

    @Test
    public void shouldFilter_blackListedMethod() {
        given(request.getRequestURI()).willReturn(WHITE_LISTED_ENDPOINT);
        given(request.getMethod()).willReturn("not-white-listed-method");

        boolean result = underTest.shouldFilter();

        assertThat(result).isTrue();
        verify(requestContext).addZuulRequestHeader(ACCESS_TOKEN_HEADER, "");
    }

    @Test
    public void run() {
        underTest.run();

        verify(authenticationService).authenticate(requestContext);
    }
}