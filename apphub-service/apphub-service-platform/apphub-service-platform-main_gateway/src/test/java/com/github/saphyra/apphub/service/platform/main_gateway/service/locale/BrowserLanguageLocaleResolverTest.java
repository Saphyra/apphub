package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BrowserLanguageLocaleResolverTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private BrowserLanguageLocaleResolver underTest;

    @Mock
    private HttpHeaders httpHeaders;

    @Test
    public void headerNotFound() {
        assertThat(underTest.getLocale(httpHeaders)).isEmpty();
    }

    @Test
    public void headerValueNotSupported() {
        given(httpHeaders.getFirst(Constants.BROWSER_LANGUAGE_HEADER)).willReturn(LOCALE);

        assertThat(underTest.getLocale(httpHeaders)).isEmpty();
    }

    @Test
    public void localeResolved() {
        given(httpHeaders.getFirst(Constants.BROWSER_LANGUAGE_HEADER)).willReturn(LOCALE);
        given(commonConfigProperties.getSupportedLocales()).willReturn(Arrays.asList(LOCALE));

        assertThat(underTest.getLocale(httpHeaders)).contains(LOCALE);
    }
}