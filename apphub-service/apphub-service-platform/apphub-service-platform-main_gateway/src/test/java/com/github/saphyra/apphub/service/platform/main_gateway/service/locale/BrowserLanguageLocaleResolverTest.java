package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BrowserLanguageLocaleResolverTest {
    private static final String LOCALE = "locale";

    @Mock
    private SupportedLocales supportedLocales;

    @InjectMocks
    private BrowserLanguageLocaleResolver underTest;

    @Mock
    private HttpServletRequest request;

    @Test
    public void getLocale_nullHeader() {
        given(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER)).willReturn(null);

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale_notSupported() {
        given(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER)).willReturn(LOCALE);
        given(supportedLocales.isSupported(LOCALE)).willReturn(false);

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale() {
        given(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER)).willReturn(LOCALE);
        given(supportedLocales.isSupported(LOCALE)).willReturn(true);

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).contains(LOCALE);
    }
}