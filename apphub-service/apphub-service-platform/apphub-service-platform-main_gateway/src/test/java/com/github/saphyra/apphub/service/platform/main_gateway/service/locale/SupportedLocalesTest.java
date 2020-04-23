package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SupportedLocalesTest {
    private static final String SUPPORTED_LOCALE = "supported-locale";

    private SupportedLocales underTest;

    @Before
    public void setUp() {
        underTest = new SupportedLocales();
        underTest.setSupportedLocales(Arrays.asList(SUPPORTED_LOCALE));
    }

    @Test
    public void supportedLocale() {
        boolean result = underTest.isSupported(SUPPORTED_LOCALE);

        assertThat(result).isTrue();
    }

    @Test
    public void unsupportedLocale() {
        boolean result = underTest.isSupported("sgwr");

        assertThat(result).isFalse();
    }
}