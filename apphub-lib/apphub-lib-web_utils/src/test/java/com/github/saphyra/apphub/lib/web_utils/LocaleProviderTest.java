package com.github.saphyra.apphub.lib.web_utils;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LocaleProviderTest {
    private static final String LOCALE = "locale";

    @Mock
    private RequestContextProvider requestContextProvider;

    @InjectMocks
    private LocaleProvider underTest;

    @Mock
    private HttpServletRequest request;

    @Before
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

    @Test
    public void getLocaleValidated_notFound() {
        given(request.getHeader(Constants.LOCALE_HEADER)).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.getLocaleValidated());

        ExceptionValidator.validateLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND);
    }

    @Test
    public void getLocaleValidated() {
        given(request.getHeader(Constants.LOCALE_HEADER)).willReturn(LOCALE);

        String result = underTest.getLocaleValidated();

        assertThat(result).isEqualTo(LOCALE);
    }
}