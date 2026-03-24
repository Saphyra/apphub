package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

@Component
public class MetricPropertyDao extends InMemoryDao<MetricPropertyEntity, MetricProperty, MetricPropertyId, MetricPropertyRepository> {
    private final UuidConverter uuidConverter;

    MetricPropertyDao(MetricPropertyConverter converter, MetricPropertyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository, true);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected MetricPropertyId extractId(MetricProperty metricProperty) {
        return MetricPropertyId.builder()
            .metricId(uuidConverter.convertDomain(metricProperty.getMetricId()))
            .property(metricProperty.getProperty())
            .build();
    }
}
