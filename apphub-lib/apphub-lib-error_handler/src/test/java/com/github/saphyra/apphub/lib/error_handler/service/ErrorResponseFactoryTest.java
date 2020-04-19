package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorResponseFactoryTest {
    private static final String LOCALE = "locale";
    private static final String ERROR_CODE = "error-code";
    private static final String LOCALIZED_MESSAGE = "localized-message";
    public static final String LOCALE_NOT_FOUND_ERROR_CODE = "LOCALE_NOT_FOUND";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private LocalizedMessageProvider localizedMessageProvider;

    @InjectMocks
    private ErrorResponseFactory underTest;

    @Mock
    private Map<String, String> params;

    @Test
    public void createErrorResponse() {
        given(localeProvider.getLocale()).willReturn(Optional.of(LOCALE));
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ERROR_CODE, params)).willReturn(LOCALIZED_MESSAGE);

        ErrorResponse result = underTest.create(HttpStatus.BAD_REQUEST, ERROR_CODE, params);

        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(result.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getParams()).isEqualTo(params);
    }

    @Test
    public void createLocaleNotFoundErrorResponse() {
        given(localeProvider.getLocale()).willReturn(Optional.empty());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, LOCALE_NOT_FOUND_ERROR_CODE, new HashMap<>())).willReturn(LOCALIZED_MESSAGE);

        ErrorResponse result = underTest.create(HttpStatus.UNAUTHORIZED, ERROR_CODE, params);

        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getErrorCode()).isEqualTo(LOCALE_NOT_FOUND_ERROR_CODE);
        assertThat(result.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getParams()).isEqualTo(new HashMap<>());
    }
}