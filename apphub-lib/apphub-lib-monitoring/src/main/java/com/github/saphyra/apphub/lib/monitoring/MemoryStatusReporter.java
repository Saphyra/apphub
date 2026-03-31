package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MemoryStatusReporter {
    private static final String FUNCTIONALITY_MEMORY_STATUS = "MEMORY_STATUS";
    private static final String KEY_AVAILABLE_MEMORY = "availableMemory";
    private static final String KEY_ALLOCATED_MEMORY = "allocatedMemory";
    private static final String KEY_USED_MEMORY = "usedMemory";
    private static final long BYTES_IN_MEGABYTE = 1024L * 1024L;

    private final MetricRegistry metricRegistry;

    public void reportMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / BYTES_IN_MEGABYTE;
        long freeMemory = runtime.freeMemory() / BYTES_IN_MEGABYTE;
        long maxMemory = runtime.maxMemory() / BYTES_IN_MEGABYTE;

        metricRegistry.reportMetric(
            Feature.MEMORY_MONITORING,
            FUNCTIONALITY_MEMORY_STATUS,
            List.of(
                MetricPropertyModel.builder()
                    .key(KEY_AVAILABLE_MEMORY)
                    .value((double) maxMemory)
                    .aggregationStrategy(AggregationStrategy.MAX)
                    .build(),
                MetricPropertyModel.builder()
                    .key(KEY_ALLOCATED_MEMORY)
                    .value((double) totalMemory)
                    .aggregationStrategy(AggregationStrategy.AVERAGE)
                    .build(),
                MetricPropertyModel.builder()
                    .key(KEY_USED_MEMORY)
                    .value((double) totalMemory - freeMemory)
                    .aggregationStrategy(AggregationStrategy.AVERAGE)
                    .build()
            )
        );
    }
}
