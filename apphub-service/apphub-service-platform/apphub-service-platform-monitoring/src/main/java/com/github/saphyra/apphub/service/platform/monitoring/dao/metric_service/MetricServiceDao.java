package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.lib.common_util.dao.InMemoryDao;
import org.springframework.stereotype.Component;

@Component
public class MetricServiceDao extends InMemoryDao<MetricServiceEntity, MetricService, MetricServiceEntity, MetricServiceRepository> {
    MetricServiceDao(MetricServiceConverter converter, MetricServiceRepository repository) {
        super(converter, repository, true);
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    @Override
    protected MetricServiceEntity extractId(MetricService metricService) {
        return converter.convertDomain(metricService);
    }
}
