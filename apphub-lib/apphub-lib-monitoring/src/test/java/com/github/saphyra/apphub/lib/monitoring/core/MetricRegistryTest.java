package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MetricRegistryTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now().withNano(0);
    private static final String FUNCTIONALITY = "functionality";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PutMetricRequestFactory putMetricRequestFactory;

    @InjectMocks
    private MetricRegistry underTest;

    @Mock
    private PutMetricsRequest request;

    @Mock
    private MetricPropertyModel propertyModel;

    @Captor
    private ArgumentCaptor<List<MetricPropertyModel>> argumentCaptor;

    @Test
    void reportMetrics() throws NoSuchFieldException, IllegalAccessException {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(putMetricRequestFactory.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, CURRENT_TIME.withNano(0), List.of(propertyModel))).willReturn(request);

        underTest.reportMetric(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, List.of(propertyModel));

        assertThat(ReflectionUtils.<List<PutMetricsRequest>>getFieldValue(underTest, "registry")).containsExactly(request);
    }

    @Test
    void getMetricsToSend() throws NoSuchFieldException, IllegalAccessException {
        given(dateTimeUtil.getCurrentDateTime())
            .willReturn(CURRENT_TIME)
            .willReturn(CURRENT_TIME.plusSeconds(1));

        given(putMetricRequestFactory.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, CURRENT_TIME.withNano(0), List.of(propertyModel))).willReturn(request);
        given(request.getTimestamp()).willReturn(CURRENT_TIME);
        underTest.reportMetric(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, List.of(propertyModel));

        given(putMetricRequestFactory.create(eq(Feature.MONITORING_METRICS), eq(FUNCTIONALITY_METRIC_COUNT), eq(CURRENT_TIME), any())).willReturn(request);

        assertThat(underTest.getMetricsToSend()).hasSize(2);

        assertThat(ReflectionUtils.<List<PutMetricsRequest>>getFieldValue(underTest, "registry")).isEmpty();

        then(putMetricRequestFactory).should().create(eq(Feature.MONITORING_METRICS), eq(FUNCTIONALITY_METRIC_COUNT), eq(CURRENT_TIME), argumentCaptor.capture());
        Map<String, MetricPropertyModel> properties = argumentCaptor.getValue()
            .stream()
            .collect(Collectors.toMap(MetricPropertyModel::getKey, m -> m));

        assertThat(properties.get(KEY_AVERAGE_SIZE))
            .returns(1.0, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.AVERAGE, MetricPropertyModel::getAggregationStrategy);

        assertThat(properties.get(KEY_MAX_SIZE))
            .returns(1.0, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.MAX, MetricPropertyModel::getAggregationStrategy);
    }
}