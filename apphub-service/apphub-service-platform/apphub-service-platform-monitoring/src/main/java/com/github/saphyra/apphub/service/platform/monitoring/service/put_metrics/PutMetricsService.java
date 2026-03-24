package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PutMetricsService {
    private final PutMetricsRequestValidator putMetricsRequestValidator;

    public void putMetrics(PutMetricsRequest metrics) {
        putMetricsRequestValidator.validate(metrics);
    }
}
