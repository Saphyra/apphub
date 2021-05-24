package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CustomLocaleProviderTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private CustomLocaleProvider underTest;

    @Test
    public void localeFound() {
        given(localeProvider.getLocale()).willReturn(Optional.of(LOCALE));

        String result = underTest.getLocale();

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void localeNotFound() {
        given(localeProvider.getLocale()).willReturn(Optional.empty());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        String result = underTest.getLocale();

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void exception() {
        given(localeProvider.getLocale()).willThrow(new RuntimeException());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        String result = underTest.getLocale();

        assertThat(result).isEqualTo(LOCALE);
    }
}