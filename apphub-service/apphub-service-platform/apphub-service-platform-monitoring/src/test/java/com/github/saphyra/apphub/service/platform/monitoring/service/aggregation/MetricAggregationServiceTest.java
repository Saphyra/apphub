package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataFactory;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider.MetricAggregationDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MetricAggregationServiceTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now()
        .withNano(0);
    private static final LocalDateTime OLDEST_TIMESTAMP = CURRENT_TIME.minusSeconds(1);
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";
    private static final String KEY = "key";
    private static final Double VALUE = 1.0;

    @Mock
    private MetricAggregationDataProvider dataProvider;

    @Mock
    private MetricDataDao metricDataDao;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private MetricDataFactory metricDataFactory;

    @Mock
    private MetricPropertyAggregator metricPropertyAggregator;

    private MetricAggregationService underTest;

    @Mock
    private MetricData oldestMetricData;

    @Mock
    private MetricData toAggregate;

    @Mock
    private MetricData aggregated;

    @BeforeEach
    void setUp() {
        given(dataProvider.getType()).willReturn(MetricDataType.SECOND);

        underTest = new MetricAggregationService(
            List.of(dataProvider),
            metricDataDao,
            executorServiceBean,
            metricPropertyAggregator,
            metricDataFactory
        );
    }

    @Test
    void aggregate() {
        given(metricDataDao.findOldest(MetricDataType.SECOND)).willReturn(Optional.of(oldestMetricData));
        given(oldestMetricData.getTimestamp()).willReturn(OLDEST_TIMESTAMP);
        given(dataProvider.getExpirationTime()).willReturn(CURRENT_TIME);
        given(dataProvider.step(CURRENT_TIME)).willReturn(OLDEST_TIMESTAMP);
        given(dataProvider.step(OLDEST_TIMESTAMP)).willReturn(OLDEST_TIMESTAMP.minusSeconds(1));
        given(metricDataDao.getByTypeBetween(MetricDataType.SECOND, OLDEST_TIMESTAMP, CURRENT_TIME)).willReturn(List.of(toAggregate));
        given(toAggregate.getMetricId()).willReturn(METRIC_ID);
        given(toAggregate.getService()).willReturn(SERVICE);
        Map<String, Double> properties = Map.of(KEY, VALUE);
        given(metricPropertyAggregator.aggregateProperties(METRIC_ID, List.of(toAggregate))).willReturn(properties);
        given(dataProvider.getResultType()).willReturn(MetricDataType.MINUTE);
        given(metricDataFactory.create(METRIC_ID, SERVICE, CURRENT_TIME, MetricDataType.MINUTE, properties)).willReturn(aggregated);

        underTest.aggregate(MetricDataType.SECOND);

        then(metricDataDao).should().save(aggregated);
        then(metricDataDao).should().deleteAll(List.of(toAggregate));
    }
}