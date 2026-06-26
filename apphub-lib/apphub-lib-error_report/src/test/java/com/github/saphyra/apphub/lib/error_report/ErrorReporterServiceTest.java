package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.etc.admin_panel.client.ErrorReporterClient;
import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ErrorReporterServiceTest {
    private static final String FUNCTIONALITY = "report";
    private static final String COUNT = "count";
    private static final String MESSAGE = "message";

    @Mock
    private ErrorReporterClient errorReporterClient;

    @Mock
    private ErrorReportFactory errorReportFactory;

    @Mock
    private MetricRegistry metricRegistry;

    @InjectMocks
    private ErrorReporterService underTest;

    @Captor
    private ArgumentCaptor<List<MetricPropertyModel>> propertyCaptor;

    @Mock
    private ErrorReport model;

    @Mock
    private RuntimeException exception;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    public void reportException() {
        given(errorReportFactory.create(HttpStatus.NOT_FOUND, errorResponse, exception)).willReturn(model);

        underTest.report(HttpStatus.NOT_FOUND, errorResponse, exception);

        verifyMetricReported();
        verify(errorReporterClient).reportError(model);
    }

    @Test
    public void reportException_error() {
        given(errorReportFactory.create(HttpStatus.NOT_FOUND, errorResponse, exception)).willThrow(exception);

        underTest.report(HttpStatus.NOT_FOUND, errorResponse, exception);

        //No exception thrown
        verifyMetricReported();
        verifyNoInteractions(errorReporterClient);
    }

    @Test
    public void reportMessage() {
        given(errorReportFactory.create(MESSAGE)).willReturn(model);

        underTest.report(MESSAGE);

        verifyMetricReported();
        verify(errorReporterClient).reportError(model);
    }

    @Test
    public void reportMessage_error() {
        given(errorReportFactory.create(MESSAGE)).willThrow(exception);

        underTest.report(MESSAGE);

        //No exception thrown
        verifyMetricReported();
        verifyNoInteractions(errorReporterClient);
    }

    @Test
    public void reportMessageAndException() {
        given(errorReportFactory.create(MESSAGE, exception)).willReturn(model);

        underTest.report(MESSAGE, exception);

        verifyMetricReported();
        verify(errorReporterClient).reportError(model);
    }

    private void verifyMetricReported() {
        then(metricRegistry).should().reportMetric(
            eq(Feature.ERROR_REPORT),
            eq(FUNCTIONALITY),
            propertyCaptor.capture()
        );

        CustomAssertions.singleListAssertThat(propertyCaptor.getValue())
            .returns(COUNT, MetricPropertyModel::getKey)
            .returns(1d, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.SUM, MetricPropertyModel::getAggregationStrategy);
    }
}