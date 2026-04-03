package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class SecondMetricAggregationDataProvider implements MetricAggregationDataProvider {
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringProperties monitoringProperties;

    @Override
    public MetricDataType getType() {
        return MetricDataType.SECOND;
    }

    @Override
    public LocalDateTime getExpirationTime() {
        Duration expirationDuration = monitoringProperties.getAggregation().get(getType()).getExpirationDuration();
        return dateTimeUtil.getCurrentDateTime()
            .withNano(0)
            .withSecond(0)
            .minus(expirationDuration);
    }

    @Override
    public LocalDateTime step(LocalDateTime reference) {
        return reference.minus(monitoringProperties.getAggregation().get(getType()).getStepDuration());
    }

    @Override
    public MetricDataType getResultType() {
        return MetricDataType.MINUTE;
    }
}
