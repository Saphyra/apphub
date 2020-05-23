package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.ApphubLocaleResolver;
import com.netflix.zuul.context.RequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocaleCookieFilterTest {
    private static final String LOCALE = "locale";
    @Mock
    private ApphubLocaleResolver localeResolver;

    @InjectMocks
    private LocaleCookieFilter underTest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void run() {
        RequestContext.testSetCurrentContext(requestContext);
        given(requestContext.getRequest()).willReturn(request);
        given(requestContext.getResponse()).willReturn(response);
        given(localeResolver.getLocale(request)).willReturn(LOCALE);

        underTest.run();

        ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argumentCaptor.capture());
        Cookie cookie = argumentCaptor.getValue();
        assertThat(cookie.getName()).isEqualTo(Constants.LOCALE_COOKIE);
        assertThat(cookie.getValue()).isEqualTo(LOCALE);
        assertThat(cookie.isHttpOnly()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(Integer.MAX_VALUE);
    }
}