package com.github.saphyra.apphub.lib.monitoring.instrument;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;

import java.util.List;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVERAGE_TIME;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_FAILURE_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_MAX_TIME;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_MIN_TIME;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_SUCCESS_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_TOTAL_COUNT;

public interface MetricMapper {
    Double VALUE_1 = 1d;
    Double VALUE_0 = 0d;

    default List<MetricPropertyModel> map(Feature feature, String functionality, Object result, boolean success, double time) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_TOTAL_COUNT)
                .value(VALUE_1)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_SUCCESS_COUNT)
                .value(success ? VALUE_1 : VALUE_0)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_FAILURE_COUNT)
                .value(success ? VALUE_0 : VALUE_1)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MIN_TIME)
                .value(time)
                .aggregationStrategy(AggregationStrategy.MIN)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_TIME)
                .value(time)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_AVERAGE_TIME)
                .value(time)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build()
        );
    }
}
