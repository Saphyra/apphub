package com.github.saphyra.apphub.lib.monitoring.memory;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_ALLOCATED_MEMORY;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVAILABLE_MEMORY;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_USED_MEMORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemoryStatusReporterTest {
    @Mock
    private MetricRegistry metricRegistry;

    @InjectMocks
    private MemoryStatusReporter underTest;

    @Captor
    private ArgumentCaptor<List<MetricPropertyModel>> argumentCaptor;

    @Test
    void reportMemoryStatus() {
        underTest.reportMemoryStatus();

        then(metricRegistry).should().reportMetric(eq(Feature.MEMORY_MONITORING), eq("MEMORY_STATUS"), argumentCaptor.capture());

        List<MetricPropertyModel> metricPropertyModels = argumentCaptor.getValue();
        assertThat(metricPropertyModels)
            .extracting(MetricPropertyModel::getKey)
            .containsExactlyInAnyOrder(KEY_AVAILABLE_MEMORY, KEY_ALLOCATED_MEMORY, KEY_USED_MEMORY);

        Map<String, AggregationStrategy> aggregationStrategyMap = metricPropertyModels.stream()
            .collect(Collectors.toMap(MetricPropertyModel::getKey, MetricPropertyModel::getAggregationStrategy));

        assertThat(aggregationStrategyMap.get(KEY_AVAILABLE_MEMORY)).isEqualTo(AggregationStrategy.MAX);
        assertThat(aggregationStrategyMap.get(KEY_ALLOCATED_MEMORY)).isEqualTo(AggregationStrategy.AVERAGE);
        assertThat(aggregationStrategyMap.get(KEY_USED_MEMORY)).isEqualTo(AggregationStrategy.AVERAGE);
    }
}