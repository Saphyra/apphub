package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
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
    private final ExceptionMapper exceptionMapper;

    ErrorReportModel create(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        ExceptionModel exceptionModel = exceptionMapper.map(exception);
        return ErrorReportModel.builder()
            .createdAt(dateTimeUtil.getCurrentDate())
            .message(safeConvert(exception, throwable -> String.format("%s on thread %s: %s", exceptionModel.getType(), exceptionModel.getThread(), throwable.getMessage()), "No message"))
            .responseStatus(status.value())
            .responseBody(objectMapperWrapper.writeValueAsString(errorResponse))
            .exception(exceptionModel)
            .build();
    }

    ErrorReportModel create(String message) {
        return ErrorReportModel.builder()
            .createdAt(dateTimeUtil.getCurrentDate())
            .message(message)
            .build();
    }
}
