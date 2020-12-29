package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
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
public class ApphubLocaleResolverTest {
    private static final String LOCALE = "locale";

    @Mock
    private BrowserLanguageLocaleResolver browserLanguageLocaleResolver;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private UserSettingLocaleResolver userSettingLocaleResolver;

    @InjectMocks
    private ApphubLocaleResolver underTest;

    @Mock
    private HttpServletRequest request;

    @Test
    public void getFromUserSettings() {
        given(userSettingLocaleResolver.getLocale(request)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(request);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void getFromCookie() {
        given(userSettingLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(request)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(request);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void getFromBrowserLanguage() {
        given(userSettingLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(browserLanguageLocaleResolver.getLocale(request)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(request);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void getDefault() {
        given(userSettingLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(browserLanguageLocaleResolver.getLocale(request)).willReturn(Optional.empty());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        String result = underTest.getLocale(request);

        assertThat(result).isEqualTo(LOCALE);
    }
}