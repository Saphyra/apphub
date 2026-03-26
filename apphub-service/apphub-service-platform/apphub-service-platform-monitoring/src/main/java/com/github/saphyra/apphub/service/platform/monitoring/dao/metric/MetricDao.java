package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class MetricDao extends InMemoryDao<MetricEntity, Metric, String, MetricRepository> {
    private final UuidConverter uuidConverter;
    private final IdGenerator idGenerator;

    MetricDao(MetricConverter converter, MetricRepository repository, UuidConverter uuidConverter, IdGenerator idGenerator) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.idGenerator = idGenerator;

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
                Metric metric = Metric.builder()
                    .metricId(idGenerator.randomUuid())
                    .feature(feature)
                    .functionality(functionality)
                    .build();
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
            .filter(metric -> metric.getFeature() ==feature)
            .map(Metric::getFunctionality)
            .distinct()
            .toList();
    }

    public Metric findByIdValidated(Feature feature, @Nullable String functionality) {
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

    public Metric findByIdValidated(UUID metricId) {
        return cache.values()
            .stream()
            .filter(metric -> metric.getMetricId().equals(metricId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notFound("Metric not found by id " + metricId));
    }
}
