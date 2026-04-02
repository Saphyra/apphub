package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.test.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class MetricServiceRepositoryTest {
    private static final String METRIC_ID_1 = "metric-id-1";
    private static final String METRIC_ID_2 = "metric-id-2";
    private static final String SERVICE_1 = "service-1";
    private static final String SERVICE_2 = "service-2";

    @Autowired
    private MetricServiceRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByServiceNotIn() {
        MetricServiceEntity entity1 = MetricServiceEntity.builder()
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .build();
        underTest.save(entity1);
        MetricServiceEntity entity2 = MetricServiceEntity.builder()
            .metricId(METRIC_ID_2)
            .service(SERVICE_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByServiceNotIn(List.of(SERVICE_1));

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByMetricIdsNotIn() {
        MetricServiceEntity entity1 = MetricServiceEntity.builder()
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .build();
        underTest.save(entity1);
        MetricServiceEntity entity2 = MetricServiceEntity.builder()
            .metricId(METRIC_ID_2)
            .service(SERVICE_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByMetricIdsNotIn(List.of(METRIC_ID_1));

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}