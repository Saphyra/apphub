package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class MetricPropertyRepositoryTest {
    private static final String METRIC_ID_1 = "metric-id-1";
    private static final String METRIC_ID_2 = "metric-id-2";
    private static final String PROPERTY_1 = "property-1";

    @Autowired
    private MetricPropertyRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByMetricIdsNotIn() {
        MetricPropertyEntity entity1 = MetricPropertyEntity.builder()
            .id(MetricPropertyId.builder()
                .metricId(METRIC_ID_1)
                .property(PROPERTY_1)
                .build())
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();
        underTest.save(entity1);
        MetricPropertyEntity entity2 = MetricPropertyEntity.builder()
            .id(MetricPropertyId.builder()
                .metricId(METRIC_ID_2)
                .property(PROPERTY_1)
                .build())
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();
        underTest.save(entity2);

        underTest.deleteByMetricIdsNotIn(List.of(METRIC_ID_1));

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}