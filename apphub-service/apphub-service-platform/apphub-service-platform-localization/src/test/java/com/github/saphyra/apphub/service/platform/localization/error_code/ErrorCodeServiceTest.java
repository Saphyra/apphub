package com.github.saphyra.apphub.service.platform.localization.error_code;

import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.error_handler.service.error_report.ErrorReporterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorCodeServiceTest {
    private static final String ERROR_CODE = "error-code";
    private static final String TRANSLATION = "translation";
    private static final String LOCALE = "locale";

    @Mock
    private ContentLoaderFactory contentLoaderFactory;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private ErrorCodeService underTest;

    @Before

    public void setUp() {
        ErrorCodeLocalization errorCodeLocalization = new ErrorCodeLocalization();
        errorCodeLocalization.put(ERROR_CODE, TRANSLATION);
        underTest.put(LOCALE, errorCodeLocalization);
    }

    @Test
    public void getByLocaleAndErrorCode_unknownLocale() {
        String result = underTest.getByLocaleAndErrorCode(ERROR_CODE, "unknown locale");

        assertThat(result).isEqualTo("error-code could not be translated.");
    }

    @Test
    public void getByLocaleAndErrorCode_unknownErrorCode() {
        String result = underTest.getByLocaleAndErrorCode("error-codes", LOCALE);

        assertThat(result).isEqualTo("error-codes could not be translated.");
        verify(errorReporterService).report("Localization not found for errorCode error-codes and locale " + LOCALE);
    }

    @Test
    public void getByLocaleAndErrorCode() {
        String result = underTest.getByLocaleAndErrorCode(ERROR_CODE, LOCALE);

        assertThat(result).isEqualTo(TRANSLATION);
    }
}