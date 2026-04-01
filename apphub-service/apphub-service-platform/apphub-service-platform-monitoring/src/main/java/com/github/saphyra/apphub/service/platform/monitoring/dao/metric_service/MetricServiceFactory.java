package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MetricServiceFactory {
    public MetricService create(UUID metricId, String service){
        return MetricService.builder()
            .metricId(metricId)
            .service(service)
            .build();
    }
}
