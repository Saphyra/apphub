package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

@Component
public class MetricDao extends InMemoryDao<MetricEntity, Metric, String, MetricRepository> {
    private final UuidConverter uuidConverter;

    MetricDao(MetricConverter converter, MetricRepository repository, UuidConverter uuidConverter) {
        super(converter, repository, true);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected String extractId(Metric metric) {
        return uuidConverter.convertDomain(metric.getMetricId());
    }
}
