package com.github.saphyra.apphub.service.etc.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.GetErrorReportsRequest;
import com.github.saphyra.apphub.service.etc.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportOverviewSpecificationFactory {
    private final CustomLocalDateTimeParser localDateTimeParser;

    public ErrorReportOverviewSpecification create(GetErrorReportsRequest request) {
        return ErrorReportOverviewSpecification.builder()
            .message(request.getMessage())
            .statusCode(request.getStatusCode())
            .startTime(parseTime(request.getStartTime()))
            .endTime(parseTime(request.getEndTime()))
            .status(Optional.ofNullable(request.getStatus()).map(ErrorReportStatus::valueOf).orElse(null))
            .service(request.getService())
            .build();
    }

    private LocalDateTime parseTime(String time) {
        return Optional.ofNullable(time)
            .filter(s -> !isBlank(s))
            .map(localDateTimeParser::parse)
            .orElse(null);
    }
}
