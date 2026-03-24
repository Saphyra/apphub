package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MetricServiceConverter extends ConverterBase<MetricServiceEntity, MetricService> {
    private final UuidConverter uuidConverter;

    @Override
    protected MetricServiceEntity processDomainConversion(MetricService domain) {
        return MetricServiceEntity.builder()
            .metricId(uuidConverter.convertDomain(domain.getMetricId()))
            .service(domain.getService())
            .build();
    }

    @Override
    protected MetricService processEntityConversion(MetricServiceEntity entity) {
        return MetricService.builder()
            .metricId(uuidConverter.convertEntity(entity.getMetricId()))
            .service(entity.getService())
            .build();
    }
}
