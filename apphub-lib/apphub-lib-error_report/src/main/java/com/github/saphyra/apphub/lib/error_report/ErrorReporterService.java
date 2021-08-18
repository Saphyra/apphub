package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorReporterService {
    private final ErrorReporterClient errorReporterClient;
    private final ErrorReportModelFactory errorReportFactory;
    private final CommonConfigProperties commonConfigProperties;

    public void report(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        try {
            ErrorReportModel model = errorReportFactory.create(status, errorResponse, exception);
            errorReporterClient.reportError(model, commonConfigProperties.getDefaultLocale());
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }

    public void report(String message) {
        try {
            ErrorReportModel model = errorReportFactory.create(message);
            errorReporterClient.reportError(model, commonConfigProperties.getDefaultLocale());
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }

    public void report(String message, Throwable exception) {
        try {
            ErrorReportModel model = errorReportFactory.create(message, exception);
            errorReporterClient.reportError(model, commonConfigProperties.getDefaultLocale());
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }
}
