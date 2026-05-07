package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ErrorResponseFactoryTest {
    private static final String LOCALE = "locale";
    private static final ErrorCode ERROR_CODE = ErrorCode.GENERAL_ERROR;
    private static final String LOCALIZED_MESSAGE = "localized-message";

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
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ERROR_CODE, params)).willReturn(LOCALIZED_MESSAGE);

        ErrorResponseWrapper result = underTest.create(HttpStatus.BAD_REQUEST, ERROR_CODE, params);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isEqualTo(params);
    }
}