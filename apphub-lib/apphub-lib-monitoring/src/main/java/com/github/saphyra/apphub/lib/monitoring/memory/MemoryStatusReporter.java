package com.github.saphyra.apphub.lib.monitoring.memory;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_ALLOCATED_MEMORY;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVAILABLE_MEMORY;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_USED_MEMORY;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryStatusReporter {
    private static final String FUNCTIONALITY_MEMORY_STATUS = "MEMORY_STATUS";
    private static final double BYTES_IN_MEGABYTE = 1024d * 1024d;

    private final MetricRegistry metricRegistry;

    public void reportMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        double totalMemory = runtime.totalMemory() / BYTES_IN_MEGABYTE;
        double freeMemory = runtime.freeMemory() / BYTES_IN_MEGABYTE;
        double maxMemory = runtime.maxMemory() / BYTES_IN_MEGABYTE;

        metricRegistry.reportMetric(
            Feature.MEMORY_MONITORING,
            FUNCTIONALITY_MEMORY_STATUS,
            List.of(
                MetricPropertyModel.builder()
                    .key(KEY_AVAILABLE_MEMORY)
                    .value(maxMemory)
                    .aggregationStrategy(AggregationStrategy.MAX)
                    .build(),
                MetricPropertyModel.builder()
                    .key(KEY_ALLOCATED_MEMORY)
                    .value(totalMemory)
                    .aggregationStrategy(AggregationStrategy.AVERAGE)
                    .build(),
                MetricPropertyModel.builder()
                    .key(KEY_USED_MEMORY)
                    .value(totalMemory - freeMemory)
                    .aggregationStrategy(AggregationStrategy.AVERAGE)
                    .build()
            )
        );
    }
}
