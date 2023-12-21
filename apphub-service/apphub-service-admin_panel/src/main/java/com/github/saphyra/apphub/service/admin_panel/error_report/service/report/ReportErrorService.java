package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportToOverviewConverter;
import com.github.saphyra.apphub.service.admin_panel.ws.ErrorReportWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportErrorService {
    private final ErrorReportDtoFactory errorReportDtoFactory;
    private final ErrorReportDao errorReportDao;
    private final ErrorReportWebSocketHandler errorReportWebSocketHandler;
    private final ErrorReportToOverviewConverter converter;

    public void saveReport(ErrorReport model) {
        if (!isNull(model.getId())) {
            throw ExceptionFactory.invalidParam("id", "must be null");
        }

        if (isBlank(model.getMessage())) {
            throw ExceptionFactory.invalidParam("message", "must not be null or blank");
        }

        if (isBlank(model.getService())) {
            throw ExceptionFactory.invalidParam("service", "must not be null or blank");
        }

        ErrorReportDto errorReport = errorReportDtoFactory.create(model);

        errorReportDao.save(errorReport);
        log.info("ErrorReport created with id {} for message {}", errorReport.getId(), errorReport.getMessage());

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT)
            .payload(converter.convert(errorReport))
            .build();

        errorReportWebSocketHandler.sendToAllConnectedClient(event);
    }
}
