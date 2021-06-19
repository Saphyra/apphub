package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReporterServiceTest {
    private static final String LOCALE = "locale";
    private static final String MESSAGE = "message";

    @Mock
    private ErrorReporterClient errorReporterClient;

    @Mock
    private ErrorReportModelFactory errorReportFactory;

    @Mock
    private CustomLocaleProvider localeProvider;

    @InjectMocks
    private ErrorReporterService underTest;

    @Mock
    private ErrorReportModel model;

    @Mock
    private RuntimeException exception;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    public void reportException() {
        given(errorReportFactory.create(HttpStatus.NOT_FOUND, errorResponse, exception)).willReturn(model);
        given(localeProvider.getLocale()).willReturn(LOCALE);

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
        given(localeProvider.getLocale()).willReturn(LOCALE);

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