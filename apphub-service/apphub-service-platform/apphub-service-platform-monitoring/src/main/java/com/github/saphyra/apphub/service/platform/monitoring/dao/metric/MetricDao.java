package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

@Component
//TODO unit test
public class MetricDao extends InMemoryDao<MetricEntity, Metric, String, MetricRepository> {
    private final UuidConverter uuidConverter;
    private final IdGenerator idGenerator;

    MetricDao(MetricConverter converter, MetricRepository repository, UuidConverter uuidConverter, IdGenerator idGenerator) {
        super(converter, repository, true);
        this.uuidConverter = uuidConverter;
        this.idGenerator = idGenerator;
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
}
