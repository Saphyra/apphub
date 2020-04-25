package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocaleFilterTest {
    private static final String LOCALE = "locale";

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Mock
    private ApphubLocaleResolver apphubLocaleResolver;

    private LocaleFilter underTest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        RequestContext.testSetCurrentContext(requestContext);
        given(requestContext.getRequest()).willReturn(request);

        underTest = new LocaleFilter(antPathMatcher, apphubLocaleResolver);
    }

    @Test
    public void shouldFilter_resourcePath() {
        given(request.getRequestURI()).willReturn("res/asd");

        boolean result = underTest.shouldFilter();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldFilter() {
        given(request.getRequestURI()).willReturn("asd");

        boolean result = underTest.shouldFilter();

        assertThat(result).isTrue();
    }

    @Test
    public void run() {
        given(apphubLocaleResolver.getLocale(request)).willReturn(LOCALE);

        underTest.run();

        verify(requestContext).addZuulRequestHeader(Constants.LOCALE_HEADER, LOCALE);
        ;
    }
}