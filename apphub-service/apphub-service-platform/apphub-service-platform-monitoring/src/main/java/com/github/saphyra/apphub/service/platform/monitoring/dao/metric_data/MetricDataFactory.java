package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MetricDataFactory {
    private final IdGenerator idGenerator;

    public MetricData create(UUID metricId, String service, LocalDateTime timestamp, Map<String, Double> properties) {
        return create(metricId, service, timestamp, MetricDataType.SECOND, properties);
    }

    public MetricData create(UUID metricId, String service, LocalDateTime timestamp, MetricDataType type, Map<String, Double> properties) {
        return MetricData.builder()
            .metricDataId(idGenerator.randomUuid())
            .metricId(metricId)
            .service(service)
            .type(type)
            .timestamp(timestamp)
            .properties(properties)
            .build();
    }
}
