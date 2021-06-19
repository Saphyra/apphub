package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.lib.common_util.converter.NullSafeConverter.safeConvert;

@RequiredArgsConstructor
@Slf4j
@Component
class ErrorReportModelFactory {
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapperWrapper objectMapperWrapper;

    ErrorReportModel create(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        return ErrorReportModel.builder()
            .createdAt(dateTimeUtil.getCurrentDate())
            .message(safeConvert(exception, Throwable::getMessage))
            .responseStatus(status.value())
            .responseBody(objectMapperWrapper.writeValueAsString(errorResponse))
            .exception(objectMapperWrapper.writeValueAsString(exception))
            .build();
    }

    ErrorReportModel create(String message) {
        return ErrorReportModel.builder()
            .createdAt(dateTimeUtil.getCurrentDate())
            .message(message)
            .build();
    }
}
