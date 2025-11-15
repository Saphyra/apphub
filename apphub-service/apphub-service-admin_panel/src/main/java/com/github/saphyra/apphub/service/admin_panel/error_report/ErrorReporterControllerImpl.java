package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsResponse;
import com.github.saphyra.apphub.api.admin_panel.server.ErrorReporterController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.MarkErrorReportService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.details.ErrorReportDetailsQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportOverviewQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ErrorReporterControllerImpl implements ErrorReporterController {
    private final ReportErrorService reportErrorService;
    private final ErrorReportOverviewQueryService errorReportOverviewQueryService;
    private final ErrorReportDetailsQueryService errorReportDetailsQueryService;
    private final ErrorReportDao errorReportDao;
    private final MarkErrorReportService markErrorReportService;
    private final DateTimeUtil dateTimeUtil;

    @Override
    public void reportError(ErrorReport model) {
        log.info("Creating errorReport for message {}", model.getMessage());
        reportErrorService.saveReport(model);
    }

    @Override
    public GetErrorReportsResponse getErrorReports(GetErrorReportsRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the errorReports with parameters {}", accessTokenHeader.getUserId(), request);

        return errorReportOverviewQueryService.query(request);
    }

    @Override
    @Transactional
    public void deleteErrorReports(List<UUID> ids, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete error reports {}}", accessTokenHeader.getUserId(), ids);
        ids.forEach(errorReportDao::deleteById);
    }

    @Override
    public void markErrorReports(List<UUID> ids, String status, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change the status of errorReports {} to {}", accessTokenHeader.getUserId(), ids, status);
        markErrorReportService.mark(ids, status);
    }

    @Override
    public ErrorReportResponse getErrorReport(UUID id, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query errorReport {}", accessTokenHeader.getUserId(), id);
        ErrorReport errorReport = errorReportDetailsQueryService.findById(id);
        return ErrorReportResponse.builder()
            .id(errorReport.getId())
            .createdAt(dateTimeUtil.format(errorReport.getCreatedAt()))
            .message(errorReport.getMessage())
            .service(errorReport.getService())
            .responseStatus(errorReport.getResponseStatus())
            .responseBody(errorReport.getResponseBody())
            .exception(errorReport.getException())
            .status(errorReport.getStatus())
            .build();
    }

    @Override
    public void deleteReadErrorReports(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete read error reports.", accessTokenHeader.getUserId());
        errorReportDao.deleteByStatus(ErrorReportStatus.READ);
    }

    @Override
    public void deleteAll(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete all error reports.", accessTokenHeader.getUserId());
        errorReportDao.deleteAllExceptStatus(List.of(ErrorReportStatus.MARKED));
    }
}
