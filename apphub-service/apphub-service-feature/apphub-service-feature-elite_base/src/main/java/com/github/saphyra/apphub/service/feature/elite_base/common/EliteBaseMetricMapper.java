package com.github.saphyra.apphub.service.feature.elite_base.common;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.instrument.MetricMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EliteBaseMetricMapper implements MetricMapper {
    static final String KEY_DELETED_RECORDS = "deletedRecords";

    @Override
    public List<MetricPropertyModel> map(Feature feature, String functionality, Object result, boolean success, double time) {
        List<MetricPropertyModel> properties = new ArrayList<>(MetricMapper.super.map(feature, functionality, result, success, time));

        if (feature == Feature.ELITE_BASE_ORPHANED_RECORD_CLEANUP) {
            MetricPropertyModel property = MetricPropertyModel.builder()
                .key(KEY_DELETED_RECORDS)
                .value(((Number) result).doubleValue())
                .aggregationStrategy(AggregationStrategy.SUM)
                .build();
            properties.add(property);
        }

        return properties;
    }
}
