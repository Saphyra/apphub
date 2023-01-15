package com.github.saphyra.apphub.service.platform.localization;

import com.github.saphyra.apphub.service.platform.localization.error_code.ErrorCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LocalizationControllerTest {
    private static final String ERROR_CODE = "error-code";
    private static final String LOCALE = "locale";
    private static final String TRANSLATED_MESSAGE = "translated-message";

    @Mock
    private ErrorCodeService errorCodeService;

    @InjectMocks
    private LocalizationController underTest;

    @Test
    public void translate() {
        given(errorCodeService.getByLocaleAndErrorCode(ERROR_CODE, LOCALE)).willReturn(TRANSLATED_MESSAGE);

        String result = underTest.translate(ERROR_CODE, LOCALE);

        assertThat(result).isEqualTo(TRANSLATED_MESSAGE);
    }
}