package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public ErrorReport create(ErrorReportModel model) {
        return ErrorReport.builder()
            .id(getOrCreate(model.getId(), idGenerator::randomUuid))
            .createdAt(getOrCreate(model.getCreatedAt(), dateTimeUtil::getCurrentDate))
            .message(model.getMessage())
            .responseStatus(model.getResponseStatus())
            .responseBody(model.getResponseBody())
            .exception(model.getException())
            .status(ErrorReportStatus.UNREAD)
            .build();
    }

    private <T> T getOrCreate(T value, Supplier<T> supplier) {
        return Optional.ofNullable(value)
            .orElseGet(supplier);
    }
}
