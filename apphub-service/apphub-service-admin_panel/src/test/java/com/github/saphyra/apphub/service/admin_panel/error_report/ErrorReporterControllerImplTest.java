package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReporterControllerImplTest {
    @Mock
    private ReportErrorService reportErrorService;

    @InjectMocks
    private ErrorReporterControllerImpl underTest;

    @Mock
    private ErrorReportModel model;

    @Test
    public void reportError() {
        underTest.reportError(model);

        verify(reportErrorService).saveReport(model);
    }
}