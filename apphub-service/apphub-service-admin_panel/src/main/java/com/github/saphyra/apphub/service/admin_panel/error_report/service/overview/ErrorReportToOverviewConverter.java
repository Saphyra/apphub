package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportToOverviewConverter {
    public ErrorReportOverview convert(ErrorReport errorReport) {
        return ErrorReportOverview.builder()
            .id(errorReport.getId())
            .createdAt(errorReport.getCreatedAt().toString())
            .responseStatus(errorReport.getResponseStatus())
            .message(errorReport.getMessage())
            .status(errorReport.getStatus().name())
            .service(errorReport.getService())
            .build();
    }
}
