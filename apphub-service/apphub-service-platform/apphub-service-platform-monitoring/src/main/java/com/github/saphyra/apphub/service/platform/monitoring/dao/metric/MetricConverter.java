package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MetricConverter extends ConverterBase<MetricEntity, Metric> {
    private final UuidConverter uuidConverter;

    @Override
    protected MetricEntity processDomainConversion(Metric domain) {
        return MetricEntity.builder()
            .metricId(uuidConverter.convertDomain(domain.getMetricId()))
            .feature(domain.getFeature())
            .functionality(domain.getFunctionality())
            .build();
    }

    @Override
    protected Metric processEntityConversion(MetricEntity entity) {
        return Metric.builder()
            .metricId(uuidConverter.convertEntity(entity.getMetricId()))
            .feature(entity.getFeature())
            .functionality(entity.getFunctionality())
            .build();
    }
}
