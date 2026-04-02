package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MetricPropertyConverter extends ConverterBase<MetricPropertyEntity, MetricProperty> {
    private final UuidConverter uuidConverter;

    @Override
    protected MetricPropertyEntity processDomainConversion(MetricProperty domain) {
        return MetricPropertyEntity.builder()
            .id(MetricPropertyId.builder()
                .metricId(uuidConverter.convertDomain(domain.getMetricId()))
                .property(domain.getProperty())
                .build())
            .aggregationStrategy(domain.getAggregationStrategy())
            .build();
    }

    @Override
    protected MetricProperty processEntityConversion(MetricPropertyEntity entity) {
        return MetricProperty.builder()
            .metricId(uuidConverter.convertEntity(entity.getId().getMetricId()))
            .property(entity.getId().getProperty())
            .aggregationStrategy(entity.getAggregationStrategy())
            .build();
    }
}
