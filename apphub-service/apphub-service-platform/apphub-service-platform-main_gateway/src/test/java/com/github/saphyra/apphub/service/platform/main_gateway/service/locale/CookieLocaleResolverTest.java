package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.util.CookieUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CookieLocaleResolverTest {
    private static final String LOCALE = "locale";

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private CookieLocaleResolver underTest;

    @Mock
    private HttpServletRequest request;

    @Test
    public void getLocale_cookieNotPresent() {
        given(cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)).willReturn(Optional.empty());

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale_notSupported() {
        given(cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)).willReturn(Optional.of(LOCALE));
        given(commonConfigProperties.getSupportedLocales()).willReturn(Collections.emptyList());

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale() {
        given(cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)).willReturn(Optional.of(LOCALE));
        given(commonConfigProperties.getSupportedLocales()).willReturn(Arrays.asList(LOCALE));

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).contains(LOCALE);
    }
}