package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.MarkErrorReportService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.details.ErrorReportDetailsQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportOverviewQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReporterControllerImplTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String STATUS = "status";

    @Mock
    private ReportErrorService reportErrorService;

    @Mock
    private ErrorReportOverviewQueryService errorReportOverviewQueryService;

    @Mock
    private ErrorReportDetailsQueryService errorReportDetailsQueryService;

    @Mock
    private ErrorReportDao errorReportDao;

    @Mock
    private MarkErrorReportService markErrorReportService;

    @InjectMocks
    private ErrorReporterControllerImpl underTest;

    @Mock
    private ErrorReportModel model;

    @Mock
    private ErrorReportOverview errorReportOverview;

    @Mock
    private GetErrorReportsRequest getErrorReportsRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void reportError() {
        underTest.reportError(model);

        verify(reportErrorService).saveReport(model);
    }

    @Test
    public void getErrorReports() {
        given(errorReportOverviewQueryService.query(getErrorReportsRequest)).willReturn(Arrays.asList(errorReportOverview));

        List<ErrorReportOverview> result = underTest.getErrorReports(getErrorReportsRequest, accessTokenHeader);

        assertThat(result).containsExactly(errorReportOverview);
    }

    @Test
    public void getErrorReport() {
        given(errorReportDetailsQueryService.findById(ID)).willReturn(model);

        ErrorReportModel result = underTest.getErrorReport(ID, accessTokenHeader);

        assertThat(result).isEqualTo(model);
    }

    @Test
    public void deleteErrorReports() {
        underTest.deleteErrorReports(Arrays.asList(ID), accessTokenHeader);

        verify(errorReportDao).deleteById(ID);
    }

    @Test
    public void markErrorReports() {
        underTest.markErrorReports(Arrays.asList(ID), STATUS, accessTokenHeader);

        verify(markErrorReportService).mark(Arrays.asList(ID), STATUS);
    }

    @Test
    public void deleteReadErrorReports() {
        underTest.deleteReadErrorReports(accessTokenHeader);

        verify(errorReportDao).deleteByStatus(ErrorReportStatus.READ);
    }

    @Test
    public void deleteAll() {
        underTest.deleteAll(accessTokenHeader);

        verify(errorReportDao).deleteAll();
    }
}