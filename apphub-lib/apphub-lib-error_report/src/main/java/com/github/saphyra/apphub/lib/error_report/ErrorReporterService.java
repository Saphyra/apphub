package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.etc.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorReporterService {
    private static final String KEY_COUNT = "count";
    private static final String FUNCTIONALITY_REPORT = "report";

    private final ErrorReporterClient errorReporterClient;
    private final ErrorReportFactory errorReportFactory;
    private final MetricRegistry metricRegistry;

    public void report(HttpStatus status, ErrorResponse errorResponse, Throwable exception) {
        try {
            reportMetric();

            ErrorReport model = errorReportFactory.create(status, errorResponse, exception);
            errorReporterClient.reportError(model);
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }

    public void report(String message) {
        try {
            reportMetric();

            log.warn(message);

            ErrorReport model = errorReportFactory.create(message);
            errorReporterClient.reportError(model);
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }

    public void report(String message, Throwable exception) {
        try {
            reportMetric();

            log.error(message, exception);

            ErrorReport model = errorReportFactory.create(message, exception);
            errorReporterClient.reportError(model);
        } catch (Exception e) {
            log.error("Failed reporting error", e);
        }
    }

    private void reportMetric() {
        MetricPropertyModel property = MetricPropertyModel.builder()
            .key(KEY_COUNT)
            .value(1d)
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();
        metricRegistry.reportMetric(Feature.ERROR_REPORT, FUNCTIONALITY_REPORT, List.of(property));
    }
}
