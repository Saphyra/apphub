package com.github.saphyra.apphub.lib.monitoring.util;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVERAGE_SIZE;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_MAX_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryDaoMonitor {
    private final List<InMemoryDao<?, ?, ?, ?>> daos;
    private final MetricRegistry metricRegistry;

    public void report() {
        daos.forEach(this::report);
    }

    private void report(InMemoryDao<?, ?, ?, ?> inMemoryDao) {
        double cacheSize = inMemoryDao.getCacheSize();
        MetricPropertyModel maxSize = MetricPropertyModel.builder()
            .key(KEY_MAX_SIZE)
            .value(cacheSize)
            .aggregationStrategy(AggregationStrategy.MAX)
            .build();
        MetricPropertyModel averageSize = MetricPropertyModel.builder()
            .key(KEY_AVERAGE_SIZE)
            .value(cacheSize)
            .aggregationStrategy(AggregationStrategy.AVERAGE)
            .build();

        metricRegistry.reportMetric(Feature.IN_MEMORY_DAO, inMemoryDao.getClass().getSimpleName(), List.of(maxSize, averageSize));
    }
}
