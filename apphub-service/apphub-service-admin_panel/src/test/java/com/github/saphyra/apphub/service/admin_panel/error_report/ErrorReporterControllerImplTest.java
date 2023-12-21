package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.MarkErrorReportService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.details.ErrorReportDetailsQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportOverviewQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorReporterControllerImplTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String STATUS = "status";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String FORMATTED_CREATED_AT = "formatted-created-at";
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";
    private static final Integer RESPONSE_STATUS = 23;
    private static final String RESPONSE_BODY = "response-body";

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

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ErrorReporterControllerImpl underTest;

    @Mock
    private ErrorReport errorReport;

    @Mock
    private ErrorReportOverview errorReportOverview;

    @Mock
    private GetErrorReportsRequest getErrorReportsRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private ExceptionModel exceptionModel;

    @Test
    public void reportError() {
        underTest.reportError(errorReport);

        verify(reportErrorService).saveReport(errorReport);
    }

    @Test
    public void getErrorReports() {
        given(errorReportOverviewQueryService.query(getErrorReportsRequest)).willReturn(Arrays.asList(errorReportOverview));

        List<ErrorReportOverview> result = underTest.getErrorReports(getErrorReportsRequest, accessTokenHeader);

        assertThat(result).containsExactly(errorReportOverview);
    }

    @Test
    public void getErrorReport() {
        given(errorReportDetailsQueryService.findById(ID)).willReturn(errorReport);
        given(errorReport.getId()).willReturn(ID);
        given(errorReport.getCreatedAt()).willReturn(CREATED_AT);
        given(dateTimeUtil.format(CREATED_AT)).willReturn(FORMATTED_CREATED_AT);
        given(errorReport.getMessage()).willReturn(MESSAGE);
        given(errorReport.getService()).willReturn(SERVICE);
        given(errorReport.getResponseStatus()).willReturn(RESPONSE_STATUS);
        given(errorReport.getResponseBody()).willReturn(RESPONSE_BODY);
        given(errorReport.getException()).willReturn(exceptionModel);
        given(errorReport.getStatus()).willReturn(STATUS);

        ErrorReportResponse result = underTest.getErrorReport(ID, accessTokenHeader);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(FORMATTED_CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getStatus()).isEqualTo(STATUS);
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