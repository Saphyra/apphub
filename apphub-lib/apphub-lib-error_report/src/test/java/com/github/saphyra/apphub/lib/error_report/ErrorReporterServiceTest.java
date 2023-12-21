package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ErrorReporterServiceTest {
    private static final String LOCALE = "locale";
    private static final String MESSAGE = "message";

    @Mock
    private ErrorReporterClient errorReporterClient;

    @Mock
    private ErrorReportFactory errorReportFactory;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private ErrorReporterService underTest;

    @Mock
    private ErrorReport model;

    @Mock
    private RuntimeException exception;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    public void reportException() {
        given(errorReportFactory.create(HttpStatus.NOT_FOUND, errorResponse, exception)).willReturn(model);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.report(HttpStatus.NOT_FOUND, errorResponse, exception);

        verify(errorReporterClient).reportError(model, LOCALE);
    }

    @Test
    public void reportException_error() {
        given(errorReportFactory.create(HttpStatus.NOT_FOUND, errorResponse, exception)).willThrow(exception);

        underTest.report(HttpStatus.NOT_FOUND, errorResponse, exception);

        //No exception thrown
        verifyNoInteractions(errorReporterClient);
    }

    @Test
    public void reportMessage() {
        given(errorReportFactory.create(MESSAGE)).willReturn(model);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.report(MESSAGE);

        verify(errorReporterClient).reportError(model, LOCALE);
    }

    @Test
    public void reportMessage_error() {
        given(errorReportFactory.create(MESSAGE)).willThrow(exception);

        underTest.report(MESSAGE);

        //No exception thrown
        verifyNoInteractions(errorReporterClient);
    }
}