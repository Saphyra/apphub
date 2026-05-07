package com.github.saphyra.apphub.lib.web_utils;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LocaleProviderTest {
    private static final String LOCALE = "locale";

    @Mock
    private RequestContextProvider requestContextProvider;

    @InjectMocks
    private LocaleProvider underTest;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        given(requestContextProvider.getCurrentHttpRequest()).willReturn(request);
    }

    @Test
    public void getLocale_found() {
        given(request.getHeader(Constants.LOCALE_HEADER)).willReturn(LOCALE);

        Optional<String> result = underTest.getLocale();

        assertThat(result).contains(LOCALE);
    }

    @Test
    public void getLocale_blank() {
        given(request.getHeader(Constants.LOCALE_HEADER)).willReturn(" ");

        Optional<String> result = underTest.getLocale();

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale_notFound() {
        given(request.getHeader(Constants.LOCALE_HEADER)).willReturn(null);

        Optional<String> result = underTest.getLocale();

        assertThat(result).isEmpty();
    }
}