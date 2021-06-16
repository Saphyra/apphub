package com.github.saphyra.apphub.lib.error_handler.service;

import static com.github.saphyra.apphub.lib.common_util.converter.NullSafeConverter.safeConvert;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ErrorReporterService {
    private final ErrorReporterClient errorReporterClient;
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final CustomLocaleProvider localeProvider;

    public void report(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        ErrorReportModel model = ErrorReportModel.builder()
            .createdAt(dateTimeUtil.getCurrentDate())
            .message(safeConvert(exception, Throwable::getMessage))
            .responseStatus(status.value())
            .responseBody(objectMapperWrapper.writeValueAsPrettyString(errorResponse))
            .exception(objectMapperWrapper.writeValueAsPrettyString(exception))
            .build();
        errorReporterClient.reportError(model, localeProvider.getLocale());
    }
}
