package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricFactory {
    private final IdGenerator idGenerator;

    public Metric create(Feature feature, String functionality){
        return Metric.builder()
            .metricId(idGenerator.randomUuid())
            .feature(feature)
            .functionality(functionality)
            .build();
    }
}
