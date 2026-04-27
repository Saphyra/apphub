package com.github.saphyra.apphub.lib.monitoring.core.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;

import java.util.List;
import java.util.function.Function;

public interface MetricPropertyAggregatorStrategy extends Function<List<Double>, Double> {
    AggregationStrategy getAggregationStrategy();
}
