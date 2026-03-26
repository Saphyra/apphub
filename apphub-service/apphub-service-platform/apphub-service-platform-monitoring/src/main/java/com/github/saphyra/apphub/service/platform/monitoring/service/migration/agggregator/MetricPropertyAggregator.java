package com.github.saphyra.apphub.service.platform.monitoring.service.migration.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;

import java.util.List;
import java.util.function.Function;

public interface MetricPropertyAggregator extends Function<List<Double>, Double> {
    AggregationStrategy getAggregationStrategy();
}
