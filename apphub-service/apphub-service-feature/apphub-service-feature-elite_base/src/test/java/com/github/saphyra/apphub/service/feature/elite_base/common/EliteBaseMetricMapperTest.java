package com.github.saphyra.apphub.service.feature.elite_base.common;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.saphyra.apphub.service.feature.elite_base.common.EliteBaseMetricMapper.KEY_DELETED_RECORDS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EliteBaseMetricMapperTest {
    private static final String FUNCTIONALITY = "functionality";
    private static final Object RESULT = 23;
    private static final double EXECUTION_TIME = 32.2;

    private final EliteBaseMetricMapper underTest = new EliteBaseMetricMapper();

    @Test
    void mapOrphanedRecordCleanup() {
        assertThat(underTest.map(Feature.ELITE_BASE_ORPHANED_RECORD_CLEANUP, FUNCTIONALITY, RESULT, true, EXECUTION_TIME))
            .hasSize(7)
            .contains(MetricPropertyModel.builder()
                .key(KEY_DELETED_RECORDS)
                .value(((Number) RESULT).doubleValue())
                .aggregationStrategy(AggregationStrategy.SUM)
                .build());
    }

    @Test
    void mapDefault() {
        assertThat(underTest.map(Feature.ELITE_BASE_QUERY, FUNCTIONALITY, RESULT, true, EXECUTION_TIME)).hasSize(6);
    }
}