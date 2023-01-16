package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CookieLocaleResolverTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private CookieLocaleResolver underTest;

    @Mock
    private HttpCookie cookie;

    private final MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @Test
    public void cookieNotFound() {
        assertThat(underTest.getLocale(cookies)).isEmpty();
    }

    @Test
    public void notSupportedLocale() {
        cookies.put(Constants.LOCALE_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(LOCALE);

        assertThat(underTest.getLocale(cookies)).isEmpty();
    }

    @Test
    public void resolvedFromCookie() {
        cookies.put(Constants.LOCALE_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(LOCALE);
        given(commonConfigProperties.getSupportedLocales()).willReturn(Arrays.asList(LOCALE));

        assertThat(underTest.getLocale(cookies)).contains(LOCALE);
    }
}