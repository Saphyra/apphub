package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportDtoFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public ErrorReportDto create(ErrorReport model) {
        return ErrorReportDto.builder()
            .id(getOrCreate(model.getId(), idGenerator::randomUuid))
            .createdAt(getOrCreate(model.getCreatedAt(), dateTimeUtil::getCurrentDateTime))
            .message(model.getMessage())
            .responseStatus(model.getResponseStatus())
            .responseBody(model.getResponseBody())
            .exception(model.getException())
            .status(ErrorReportStatus.UNREAD)
            .service(model.getService())
            .build();
    }

    private <T> T getOrCreate(T value, Supplier<T> supplier) {
        return Optional.ofNullable(value)
            .orElseGet(supplier);
    }
}
