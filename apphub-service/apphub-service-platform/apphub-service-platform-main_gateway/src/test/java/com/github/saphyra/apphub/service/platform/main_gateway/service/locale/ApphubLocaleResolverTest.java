package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private HttpHeaders httpHeaders;

    @Mock
    private HttpCookie cookie;

    private MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @Test
    public void resolveFromUserSettings() {
        given(userSettingLocaleResolver.getLocale(cookies)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(httpHeaders, cookies);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void resolveFromCookie() {
        given(userSettingLocaleResolver.getLocale(cookies)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(cookies)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(httpHeaders, cookies);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void resolveFromBrowserLanguage() {
        given(userSettingLocaleResolver.getLocale(cookies)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(cookies)).willReturn(Optional.empty());
        given(browserLanguageLocaleResolver.getLocale(httpHeaders)).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale(httpHeaders, cookies);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void useDefault() {
        given(userSettingLocaleResolver.getLocale(cookies)).willReturn(Optional.empty());
        given(cookieLocaleResolver.getLocale(cookies)).willReturn(Optional.empty());
        given(browserLanguageLocaleResolver.getLocale(httpHeaders)).willReturn(Optional.empty());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        String result = underTest.getLocale(httpHeaders, cookies);

        assertThat(result).isEqualTo(LOCALE);
    }
}