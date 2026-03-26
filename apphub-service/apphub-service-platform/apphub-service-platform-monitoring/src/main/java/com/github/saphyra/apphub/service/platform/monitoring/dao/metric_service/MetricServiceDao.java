package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MetricServiceDao extends InMemoryDao<MetricServiceEntity, MetricService, MetricServiceEntity, MetricServiceRepository> {
    MetricServiceDao(MetricServiceConverter converter, MetricServiceRepository repository) {
        super(converter, repository);

        load();
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    @Override
    protected MetricServiceEntity extractId(MetricService metricService) {
        return converter.convertDomain(metricService);
    }

    public List<MetricService> getByMetricId(UUID metricId) {
        return cache.values()
            .stream()
            .filter(metricService -> metricService.getMetricId().equals(metricId))
            .toList();
    }
}
