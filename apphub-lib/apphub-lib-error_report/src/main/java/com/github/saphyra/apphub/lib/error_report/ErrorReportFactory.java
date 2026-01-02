package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.NullSafeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
@Component
class ErrorReportFactory {
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapper objectMapper;
    private final ExceptionMapper exceptionMapper;
    private final CommonConfigProperties commonConfigProperties;

    ErrorReport create(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        ExceptionModel exceptionModel = exceptionMapper.map(exception);
        return ErrorReport.builder()
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .message(NullSafeConverter.safeConvert(exception, throwable -> String.format("%s on thread %s: %s", exceptionModel.getType(), exceptionModel.getThread(), throwable.getMessage()), "No message"))
            .responseStatus(status.value())
            .responseBody(objectMapper.writeValueAsString(errorResponse))
            .exception(exceptionModel)
            .service(commonConfigProperties.getApplicationName())
            .build();
    }

    ErrorReport create(String message) {
        return create(message, null);
    }

    public ErrorReport create(String message, Throwable exception) {
        ExceptionModel exceptionModel = NullSafeConverter.safeConvert(exception, exceptionMapper::map);

        return ErrorReport.builder()
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .message(message)
            .exception(exceptionModel)
            .service(commonConfigProperties.getApplicationName())
            .build();
    }
}
