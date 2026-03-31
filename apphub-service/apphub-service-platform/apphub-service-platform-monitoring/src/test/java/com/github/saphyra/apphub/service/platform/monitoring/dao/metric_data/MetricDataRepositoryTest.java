package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.test.repository.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class MetricDataRepositoryTest {
    private static final String METRIC_DATA_ID_1 = "metric-data-id-1";
    private static final String METRIC_DATA_ID_2 = "metric-data-id-2";
    private static final String METRIC_DATA_ID_3 = "metric-data-id-3";
    private static final String METRIC_DATA_ID_4 = "metric-data-id-4";
    private static final LocalDateTime REFERENCE_TIME = LocalDateTime.now();
    private static final LocalDateTime START_TIME = REFERENCE_TIME.minusMinutes(1);
    private static final LocalDateTime END_TIME = REFERENCE_TIME.plusMinutes(1);

    @Autowired
    private MetricDataRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
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
}