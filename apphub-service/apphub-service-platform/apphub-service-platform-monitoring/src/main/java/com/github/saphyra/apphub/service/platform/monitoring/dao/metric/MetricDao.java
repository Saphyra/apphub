package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MetricDao extends InMemoryDao<MetricEntity, Metric, String, MetricRepository> {
    private final UuidConverter uuidConverter;
    private final MetricFactory metricFactory;

    MetricDao(MetricConverter converter, MetricRepository repository, UuidConverter uuidConverter, MetricFactory metricFactory) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.metricFactory = metricFactory;

        load();
    }

    @Override
    protected String extractId(Metric metric) {
        return uuidConverter.convertDomain(metric.getMetricId());
    }

    public Metric findOrCreate(Feature feature, String functionality) {
        return cache.values()
            .stream()
            .filter(metric -> metric.getFeature() == feature && metric.getFunctionality().equals(functionality))
            .findFirst()
            .orElseGet(() -> {
                log.info("Creating new metric for feature {} and functionality {} as it does not exist yet. Existing entries: {}", feature, functionality, cache);

                Metric metric = metricFactory.create(feature, functionality);
                save(metric);

                return metric;
            });
    }

    public List<Feature> getFeatures() {
        return cache.values()
            .stream()
            .map(Metric::getFeature)
            .distinct()
            .toList();
    }

    public List<String> getFunctionalitiesOfFeature(Feature feature) {
        return cache.values()
            .stream()
            .filter(metric -> metric.getFeature() == feature)
            .map(Metric::getFunctionality)
            .distinct()
            .toList();
    }

    public Metric findByFeatureAndFunctionalityValidated(Feature feature, @Nullable String functionality) {
        return cache.values()
            .stream()
            .filter(metric -> metric.getFeature() == feature && (functionality == null || metric.getFunctionality().equals(functionality)))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notFound("Metric not found by feature " + feature + " and functionality " + functionality));
    }

    public List<Metric> getByFeatureAndOptionalFunctionality(Feature feature, @Nullable String functionality) {
        return cache.values()
            .stream()
            .filter(metric -> metric.getFeature() == feature && (functionality == null || metric.getFunctionality().equals(functionality)))
            .toList();
    }

    public void deleteByMetricIdNotIn(List<UUID> metricIds) {
        List<String> toRemove = cache.keySet()
            .stream()
            .filter(id -> !metricIds.contains(uuidConverter.convertEntity(id)))
            .toList();

        toRemove.forEach(id -> log.info("{} has no more records. Deleting it.", cache.get(id)));

        toRemove.forEach(cache::remove);

        repository.deleteByMetricIdNotIn(uuidConverter.convertDomain(metricIds));
    }
}
