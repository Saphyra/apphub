package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MetricDataConverter extends ConverterBase<MetricDataEntity, MetricData> {
    private final UuidConverter uuidConverter;
    private final ObjectMapper objectMapper;

    @Override
    protected MetricDataEntity processDomainConversion(MetricData domain) {
        return MetricDataEntity.builder()
            .metricDataId(uuidConverter.convertDomain(domain.getMetricDataId()))
            .metricId(uuidConverter.convertDomain(domain.getMetricId()))
            .service(domain.getService())
            .type(domain.getType())
            .createdAt(domain.getCreatedAt())
            .properties(objectMapper.writeValueAsString(domain.getProperties()))
            .build();
    }

    @Override
    protected MetricData processEntityConversion(MetricDataEntity entity) {
        TypeReference<Map<String, Double>> typeRef = new TypeReference<>() {
        };
        return MetricData.builder()
            .metricDataId(uuidConverter.convertEntity(entity.getMetricDataId()))
            .metricId(uuidConverter.convertEntity(entity.getMetricId()))
            .service(entity.getService())
            .type(entity.getType())
            .createdAt(entity.getCreatedAt())
            .properties(objectMapper.readValue(entity.getProperties(), typeRef))
            .build();
    }
}
