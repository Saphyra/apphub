package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ErrorResponseFactoryTest {
    private static final String LOCALE = "locale";
    private static final ErrorCode ERROR_CODE = ErrorCode.GENERAL_ERROR;
    private static final String LOCALIZED_MESSAGE = "localized-message";

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

    @Mock
    private HttpServletRequest request;

    @Test
    public void createErrorResponse() {
        given(localeProvider.getLocale()).willReturn(Optional.of(LOCALE));
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ERROR_CODE, params)).willReturn(LOCALIZED_MESSAGE);

        ErrorResponseWrapper result = underTest.create(HttpStatus.BAD_REQUEST, ERROR_CODE, params);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isEqualTo(params);
    }

    @Test
    public void createLocaleNotFoundErrorResponse() {
        given(localeProvider.getLocale()).willReturn(Optional.empty());
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ErrorCode.LOCALE_NOT_FOUND, new HashMap<>())).willReturn(LOCALIZED_MESSAGE);

        ErrorResponseWrapper result = underTest.create(HttpStatus.UNAUTHORIZED, ERROR_CODE, params);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ErrorCode.LOCALE_NOT_FOUND);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isEqualTo(new HashMap<>());
    }

    @Test
    public void withoutLocale_localeFound() {
        given(localeProvider.getLocale(request)).willReturn(Optional.of(LOCALE));
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ERROR_CODE, new HashMap<>())).willReturn(LOCALIZED_MESSAGE);

        ErrorResponseWrapper result = underTest.create(request, HttpStatus.UNAUTHORIZED, ERROR_CODE);
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isEmpty();
    }

    @Test
    public void withoutLocale_localeNotFound() {
        given(localeProvider.getLocale(request)).willReturn(Optional.empty());
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ErrorCode.LOCALE_NOT_FOUND, new HashMap<>())).willReturn(LOCALIZED_MESSAGE);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        ErrorResponseWrapper result = underTest.create(request, HttpStatus.UNAUTHORIZED, ERROR_CODE);
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ErrorCode.LOCALE_NOT_FOUND);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isEmpty();
    }
}