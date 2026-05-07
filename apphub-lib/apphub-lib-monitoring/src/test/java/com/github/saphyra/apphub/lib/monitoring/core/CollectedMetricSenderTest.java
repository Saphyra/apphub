package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.monitoring.core.agggregator.MetricPropertyAggregatorStrategy;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CollectedMetricSenderTest {
    private static final String FUNCTIONALITY = "functionality";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now().withNano(0);
    private static final String PROPERTY_KEY = "property-key";
    private static final Double PROPERTY_VALUE = 213.3;
    private static final PutMetricsRequest REQUEST = PutMetricsRequest.builder()
        .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
        .functionality(FUNCTIONALITY)
        .timestamp(TIMESTAMP)
        .properties(List.of(
            MetricPropertyModel.builder()
                .key(PROPERTY_KEY)
                .value(PROPERTY_VALUE)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build()
        ))
        .build();
    @Mock
    private CollectedMetricClient collectedMetricClient;

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private MetricPropertyAggregatorStrategy aggregatorStrategy;

    private CollectedMetricSender underTest;

    @Captor
    private ArgumentCaptor<List<PutMetricsRequest>> captor;

    @BeforeEach
    void setUp() {
        given(aggregatorStrategy.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        underTest = new CollectedMetricSender(collectedMetricClient, metricRegistry, List.of(aggregatorStrategy));
    }

    @Test
    void sendCollectedMetrics() {
        given(metricRegistry.getMetricsToSend()).willReturn(List.of(REQUEST, REQUEST));
        given(aggregatorStrategy.apply(List.of(PROPERTY_VALUE, PROPERTY_VALUE))).willReturn(PROPERTY_VALUE * 2);

        underTest.sendCollectedMetrics();

        then(collectedMetricClient).should().send(captor.capture());
        List<PutMetricsRequest> captured = captor.getValue();
        assertThat(captured.size()).isEqualTo(1);
        PutMetricsRequest request = captured.getFirst();

        assertThat(request)
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, PutMetricsRequest::getFeature)
            .returns(FUNCTIONALITY, PutMetricsRequest::getFunctionality)
            .returns(TIMESTAMP, PutMetricsRequest::getTimestamp);
        CustomAssertions.singleListAssertThat(request.getProperties())
            .returns(PROPERTY_KEY, MetricPropertyModel::getKey)
            .returns(PROPERTY_VALUE * 2, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.SUM, MetricPropertyModel::getAggregationStrategy);
    }
}