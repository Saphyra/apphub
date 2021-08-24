package com.github.saphyra.apphub.service.admin_panel.error_report.service.details;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportToResponseConverter {
    public ErrorReportModel convert(ErrorReport errorReport) {
        return ErrorReportModel.builder()
            .id(errorReport.getId())
            .createdAt(errorReport.getCreatedAt())
            .message(errorReport.getMessage())
            .responseStatus(errorReport.getResponseStatus())
            .responseBody(errorReport.getResponseBody())
            .exception(errorReport.getException())
            .status(errorReport.getStatus().name())
            .service(errorReport.getService())
            .build();
    }
}
