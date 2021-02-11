package com.github.saphyra.apphub.service.skyxplore.facade.filter;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.skyxplore.facade.config.CharacterExistenceWhitelistConfiguration;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CharacterExistenceFilterTest {
    private static final String REQUEST_URI = "request-uri";
    private static final String WHITE_LISTED_PATH = "white-listed-path";
    private static final String ACCESS_TOKEN_HEADER_STRING = "access-token-header-string";
    private static final String LOCALE = "locale";

    @Mock
    private SkyXploreCharacterDataApiClient characterApi;

    @Mock
    private CharacterExistenceWhitelistConfiguration configuration;

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private CharacterExistenceFilter underTest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private final WhiteListedEndpoint whiteListedEndpoint = WhiteListedEndpoint.builder()
        .path(WHITE_LISTED_PATH)
        .method(HttpMethod.POST.name())
        .build();

    @Before
    public void setUp() {
        RequestContext.testSetCurrentContext(requestContext);

        given(requestContext.getRequest()).willReturn(request);
        given(requestContext.getResponse()).willReturn(response);

        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(Constants.RESOURCE_PATH_PATTERN, REQUEST_URI)).willReturn(false);
        given(configuration.getWhiteListedEndpoints()).willReturn(Arrays.asList(whiteListedEndpoint));
        given(request.getMethod()).willReturn(HttpMethod.POST.name());

        given(antPathMatcher.match(WHITE_LISTED_PATH, REQUEST_URI)).willReturn(false);

        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    public void filterType() {
        assertThat(underTest.filterType()).isEqualTo(FilterConstants.PRE_TYPE);
    }

    @Test
    public void shouldFilter_resourcePath() {
        given(antPathMatcher.match(Constants.RESOURCE_PATH_PATTERN, REQUEST_URI)).willReturn(true);

        assertThat(underTest.shouldFilter()).isFalse();
    }

    @Test
    public void shouldFilter_whiteListed() {
        given(antPathMatcher.match(WHITE_LISTED_PATH, REQUEST_URI)).willReturn(true);

        assertThat(underTest.shouldFilter()).isFalse();
    }

    @Test
    public void shouldFilter_noCharacter() {
        given(characterApi.doesCharacterExistForUser(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(false);

        assertThat(underTest.shouldFilter()).isTrue();
    }

    @Test
    public void shouldFilter_hasCharacter() {
        given(characterApi.doesCharacterExistForUser(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(true);

        assertThat(underTest.shouldFilter()).isFalse();
    }

    @Test
    public void run_apiCall() {
        given(antPathMatcher.match(Constants.API_URI_PATTERN, REQUEST_URI)).willReturn(true);

        underTest.run();

        verify(requestContext).addZuulResponseHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        verify(requestContext).setResponseStatusCode(HttpStatus.EXPECTATION_FAILED.value());
        verify(requestContext).setSendZuulResponse(false);
    }

    @Test
    public void run_pageCall() throws IOException {
        given(antPathMatcher.match(Constants.API_URI_PATTERN, REQUEST_URI)).willReturn(false);

        underTest.run();

        verify(response).sendRedirect(Endpoints.SKYXPLORE_CHARACTER_PAGE);
    }
}