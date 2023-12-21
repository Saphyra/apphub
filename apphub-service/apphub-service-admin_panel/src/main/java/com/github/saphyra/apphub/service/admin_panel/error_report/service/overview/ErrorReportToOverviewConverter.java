package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorReportToOverviewConverter {
    private final DateTimeUtil dateTimeUtil;

    public ErrorReportOverview convert(ErrorReportDto errorReport) {
        return ErrorReportOverview.builder()
            .id(errorReport.getId())
            .createdAt(dateTimeUtil.format(errorReport.getCreatedAt()))
            .responseStatus(errorReport.getResponseStatus())
            .message(errorReport.getMessage())
            .status(errorReport.getStatus().name())
            .service(errorReport.getService())
            .build();
    }
}
