package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
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
class MetricRepositoryTest {
    private static final String METRIC_ID_1 = "metric-id-1";
    private static final String FUNCTIONALITY_1 = "functionality-1";

    @Autowired
    private MetricRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByMetricIdNotIn() {
        MetricEntity entity1 = MetricEntity.builder()
            .metricId(METRIC_ID_1)
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY_1)
            .build();
        underTest.save(entity1);
        MetricEntity entity2 = MetricEntity.builder()
            .metricId(METRIC_ID_1)
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY_1)
            .build();
        underTest.save(entity2);

        underTest.deleteByMetricIdNotIn(List.of(METRIC_ID_1));

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}