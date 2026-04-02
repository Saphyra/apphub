package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class PutMetricRequestFactory {
    PutMetricsRequest create(Feature feature, String functionality, LocalDateTime timestamp, List<MetricPropertyModel> properties) {
        return PutMetricsRequest.builder()
            .feature(feature)
            .functionality(functionality)
            .timestamp(timestamp)
            .properties(properties)
            .build();
    }
}
