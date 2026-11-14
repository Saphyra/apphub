package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricDataDaoSelector {
    private final SecondPrecisionMetricDataDao secondPrecisionMetricDataDao;
    private final MetricDataDaoImpl metricDataDao;

    public MetricDataDao selectDao(MetricDataType metricDataType) {
        return switch (metricDataType) {
            case SECOND -> secondPrecisionMetricDataDao;
            default -> metricDataDao;
        };
    }
}
