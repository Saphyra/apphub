package com.github.saphyra.apphub.lib.monitoring.util;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVERAGE_SIZE;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_MAX_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InMemoryDaoMonitorTest {
    private static final int CACHE_SIZE = 2;

    @Mock
    private InMemoryDao<?, ?, ?, ?> inMemoryDao;

    @Mock
    private MetricRegistry metricRegistry;

    private InMemoryDaoMonitor underTest;

    @Captor
    private ArgumentCaptor<List<MetricPropertyModel>> argumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new InMemoryDaoMonitor(List.of(inMemoryDao), metricRegistry);
    }

    @Test
    void report() {
        given(inMemoryDao.getCacheSize()).willReturn(CACHE_SIZE);

        underTest.report();

        then(metricRegistry).should().reportMetric(eq(Feature.IN_MEMORY_DAO), eq(inMemoryDao.getClass().getSimpleName()), argumentCaptor.capture());

        Map<String, MetricPropertyModel> properties = argumentCaptor.getValue()
            .stream()
            .collect(Collectors.toMap(MetricPropertyModel::getKey, p -> p));

        assertThat(properties.get(KEY_MAX_SIZE))
            .returns((double) CACHE_SIZE, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.MAX, MetricPropertyModel::getAggregationStrategy);

        assertThat(properties.get(KEY_AVERAGE_SIZE))
            .returns((double) CACHE_SIZE, MetricPropertyModel::getValue)
            .returns(AggregationStrategy.AVERAGE, MetricPropertyModel::getAggregationStrategy);
    }
}