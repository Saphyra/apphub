package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.etc.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
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
    private static final String MESSAGE = "message";

    @Mock
    private ErrorReporterClient errorReporterClient;

    @Mock
    private ErrorReportFactory errorReportFactory;

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

        underTest.report(HttpStatus.NOT_FOUND, errorResponse, exception);

        verify(errorReporterClient).reportError(model);
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

        underTest.report(MESSAGE);

        verify(errorReporterClient).reportError(model);
    }

    @Test
    public void reportMessage_error() {
        given(errorReportFactory.create(MESSAGE)).willThrow(exception);

        underTest.report(MESSAGE);

        //No exception thrown
        verifyNoInteractions(errorReporterClient);
    }
}