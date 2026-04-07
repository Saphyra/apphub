package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MetricPropertyDao extends InMemoryDao<MetricPropertyEntity, MetricProperty, MetricPropertyId, MetricPropertyRepository> {
    private final UuidConverter uuidConverter;

    MetricPropertyDao(MetricPropertyConverter converter, MetricPropertyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;

        load();
    }

    @Override
    protected MetricPropertyId extractId(MetricProperty metricProperty) {
        return MetricPropertyId.builder()
            .metricId(uuidConverter.convertDomain(metricProperty.getMetricId()))
            .property(metricProperty.getProperty())
            .build();
    }

    public List<MetricProperty> getByMetricId(UUID metricId) {
        return cache.values()
            .stream()
            .filter(metricProperty -> metricProperty.getMetricId().equals(metricId))
            .toList();
    }

    public void deleteByMetricIdsNotIn(List<UUID> metricIds) {
        List<MetricProperty> toRemove = cache.values()
            .stream()
            .filter(metricService -> !metricIds.contains(metricService.getMetricId()))
            .toList();

        toRemove.forEach(metricProperty -> log.info("{} has no more records. Deleting it...", metricProperty));

        toRemove.forEach(metricService -> cache.remove(extractId(metricService)));

        repository.deleteByMetricIdsNotIn(uuidConverter.convertDomain(metricIds));
    }
}
