package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class MinuteMetricAggregationDataProvider implements MetricAggregationDataProvider {
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringProperties monitoringProperties;

    @Override
    public MetricDataType getType() {
        return MetricDataType.MINUTE;
    }

    @Override
    public LocalDateTime getExpirationTime() {
        return dateTimeUtil.getCurrentDateTime()
            .withNano(0)
            .withSecond(0)
            .withMinute(0)
            .minus(monitoringProperties.getAggregation().get(getType()).getExpirationDuration());
    }

    @Override
    public LocalDateTime step(LocalDateTime reference) {
        return reference.minus(monitoringProperties.getAggregation().get(getType()).getStepDuration());
    }

    @Override
    public MetricDataType getResultType() {
        return MetricDataType.HOUR;
    }
}
