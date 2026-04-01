package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class MetricServiceDao extends InMemoryDao<MetricServiceEntity, MetricService, MetricServiceEntity, MetricServiceRepository> {
    private final UuidConverter uuidConverter;

    MetricServiceDao(MetricServiceConverter converter, MetricServiceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;

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

    public void deleteByServiceNotIn(List<String> services) {
        repository.deleteByServiceNotIn(services);
    }

    public void deleteByMetricIdsNotIn(List<UUID> metricIds) {
        repository.deleteByMetricIdsNotIn(uuidConverter.convertDomain(metricIds));
    }
}
