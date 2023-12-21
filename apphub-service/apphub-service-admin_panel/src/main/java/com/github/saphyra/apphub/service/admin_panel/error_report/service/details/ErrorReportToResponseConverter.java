package com.github.saphyra.apphub.service.admin_panel.error_report.service.details;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportToResponseConverter {
    public ErrorReport convert(ErrorReportDto errorReport) {
        return ErrorReport.builder()
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
