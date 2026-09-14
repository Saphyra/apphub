package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class MetricDataRepositoryTest {
    private static final String METRIC_DATA_ID_1 = "metric-data-id-1";
    private static final String METRIC_DATA_ID_2 = "metric-data-id-2";
    private static final String METRIC_DATA_ID_3 = "metric-data-id-3";
    private static final String METRIC_DATA_ID_4 = "metric-data-id-4";
    private static final String METRIC_DATA_ID_5 = "metric-data-id-5";
    private static final LocalDateTime REFERENCE_TIME = LocalDateTime.now();
    private static final LocalDateTime START_TIME = REFERENCE_TIME.minusMinutes(1);
    private static final LocalDateTime END_TIME = REFERENCE_TIME.plusMinutes(1);
    private static final String METRIC_ID_1 = "metric-id-1";
    private static final String METRIC_ID_2 = "metric-id-2";
    private static final String SERVICE_1 = "service-1";
    private static final String SERVICE_2 = "service-2";

    @Autowired
    private MetricDataRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByExpirationBefore() {
        MetricDataEntity entity1 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .type(MetricDataType.HOUR)
            .build();
        underTest.save(entity1);
        MetricDataEntity entity2 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .timestamp(REFERENCE_TIME.minusMinutes(1))
            .type(MetricDataType.HOUR)
            .build();
        underTest.save(entity2);

        underTest.deleteByExpirationBefore(REFERENCE_TIME);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    void getByTypeBetween() {
        MetricDataEntity matchingEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .type(MetricDataType.SECOND)
            .timestamp(REFERENCE_TIME)
            .build();
        underTest.save(matchingEntity);
        MetricDataEntity differentType = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .type(MetricDataType.MINUTE)
            .timestamp(REFERENCE_TIME)
            .build();
        underTest.save(differentType);
        MetricDataEntity tooEarly = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_3)
            .type(MetricDataType.MINUTE)
            .timestamp(START_TIME.minusSeconds(1))
            .build();
        underTest.save(tooEarly);
        MetricDataEntity tooLate = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_4)
            .type(MetricDataType.MINUTE)
            .timestamp(END_TIME.plusSeconds(1))
            .build();
        underTest.save(tooLate);

        assertThat(underTest.getByTypeBetween(MetricDataType.SECOND, START_TIME, END_TIME)).extracting(MetricDataEntity::getMetricDataId).containsExactly(METRIC_DATA_ID_1);
    }

    @Test
    void getByTypeAndMetricIdInAndServiceAfter() {
        MetricDataEntity matchingEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(matchingEntity);
        MetricDataEntity differentTypeEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .type(MetricDataType.HOUR)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentTypeEntity);
        MetricDataEntity differentMetricIdEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_3)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_2)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentMetricIdEntity);
        MetricDataEntity differentServiceEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_4)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_2)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentServiceEntity);
        MetricDataEntity expiredEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_5)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.minusSeconds(1))
            .build();
        underTest.save(expiredEntity);

        assertThat(underTest.getByTypeAndMetricIdInAndServiceAfter(MetricDataType.SECOND, List.of(METRIC_ID_1), SERVICE_1, REFERENCE_TIME))
            .extracting(MetricDataEntity::getMetricDataId)
            .containsExactly(METRIC_DATA_ID_1);
    }

    @Test
    void getByTypeAndMetricIdInAndServiceAfter_nullService() {
        MetricDataEntity matchingEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(matchingEntity);
        MetricDataEntity differentTypeEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .type(MetricDataType.HOUR)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentTypeEntity);
        MetricDataEntity differentMetricIdEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_3)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_2)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentMetricIdEntity);
        MetricDataEntity differentServiceEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_4)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_2)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(differentServiceEntity);
        MetricDataEntity expiredEntity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_5)
            .type(MetricDataType.SECOND)
            .metricId(METRIC_ID_1)
            .service(SERVICE_1)
            .timestamp(REFERENCE_TIME.minusSeconds(1))
            .build();
        underTest.save(expiredEntity);

        assertThat(underTest.getByTypeAndMetricIdInAndServiceAfter(MetricDataType.SECOND, List.of(METRIC_ID_1), null, REFERENCE_TIME))
            .extracting(MetricDataEntity::getMetricDataId)
            .containsExactlyInAnyOrder(METRIC_DATA_ID_1, METRIC_DATA_ID_4);
    }

    @Test
    void findFirstByTypeOrderByTimestampAsc() {
        MetricDataEntity oldest = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .type(MetricDataType.SECOND)
            .timestamp(REFERENCE_TIME)
            .build();
        underTest.save(oldest);
        MetricDataEntity notOldest = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .type(MetricDataType.SECOND)
            .timestamp(REFERENCE_TIME.plusSeconds(1))
            .build();
        underTest.save(notOldest);
        MetricDataEntity differentType = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_3)
            .type(MetricDataType.MINUTE)
            .timestamp(REFERENCE_TIME.minusDays(1))
            .build();
        underTest.save(differentType);

        assertThat(underTest.findFirstByTypeOrderByTimestampAsc(MetricDataType.SECOND).map(MetricDataEntity::getMetricDataId)).contains(METRIC_DATA_ID_1);
    }

    @Test
    void getServices() {
        MetricDataEntity entity1 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .service(SERVICE_1)
            .build();
        underTest.save(entity1);
        MetricDataEntity entity2 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .service(SERVICE_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getServices()).containsExactlyInAnyOrder(SERVICE_1, SERVICE_2);
    }

    @Test
    void getMetricIds() {
        MetricDataEntity entity1 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_1)
            .metricId(METRIC_ID_1)
            .build();
        underTest.save(entity1);
        MetricDataEntity entity2 = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_2)
            .metricId(METRIC_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getMetricIds()).containsExactlyInAnyOrder(METRIC_ID_1, METRIC_ID_2);
    }
}