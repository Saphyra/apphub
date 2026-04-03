package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;

import java.time.LocalDateTime;

public interface MetricAggregationDataProvider {
    MetricDataType getType();

    LocalDateTime getExpirationTime();

    LocalDateTime step(LocalDateTime reference);

    MetricDataType getResultType();
}
