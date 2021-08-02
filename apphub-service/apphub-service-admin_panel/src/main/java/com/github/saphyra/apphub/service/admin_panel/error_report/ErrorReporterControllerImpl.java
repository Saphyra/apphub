package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.MarkErrorReportService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.details.ErrorReportDetailsQueryService;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportOverviewQueryService;
import org.springframework.web.bind.annotation.RestController;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.server.ErrorReporterController;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
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

    @Override
    public void reportError(ErrorReportModel model) {
        log.info("Creating errorReport for message {}", model.getMessage());
        reportErrorService.saveReport(model);
    }

    @Override
    public List<ErrorReportOverview> getErrorReports(GetErrorReportsRequest request, AccessTokenHeader accessTokenHeader) {
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
    public ErrorReportModel getErrorReport(UUID id, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query errorReport {}", accessTokenHeader.getUserId(), id);
        return errorReportDetailsQueryService.findById(id);
    }

    @Override
    public void deleteReadErrorReports(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete read error reports.", accessTokenHeader.getUserId());
        errorReportDao.deleteByStatus(ErrorReportStatus.READ);
    }
}
