package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricDataFactoryTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String KEY = "key";
    private static final Double VALUE = 23.2;
    private static final UUID METRIC_DATA_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private MetricDataFactory underTest;

    @Test
    void createSecond() {
        given(idGenerator.randomUuid()).willReturn(METRIC_DATA_ID);

        assertThat(underTest.createSecond(METRIC_ID, SERVICE, TIMESTAMP, Map.of(KEY, VALUE)))
            .returns(METRIC_DATA_ID, MetricData::getMetricDataId)
            .returns(METRIC_ID, MetricData::getMetricId)
            .returns(SERVICE, MetricData::getService)
            .returns(TIMESTAMP, MetricData::getTimestamp)
            .returns(Map.of(KEY, VALUE), MetricData::getProperties)
            .returns(MetricDataType.SECOND, MetricData::getType);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(METRIC_DATA_ID);

        assertThat(underTest.create(METRIC_ID, SERVICE, TIMESTAMP, MetricDataType.MINUTE, Map.of(KEY, VALUE)))
            .returns(METRIC_DATA_ID, MetricData::getMetricDataId)
            .returns(METRIC_ID, MetricData::getMetricId)
            .returns(SERVICE, MetricData::getService)
            .returns(TIMESTAMP, MetricData::getTimestamp)
            .returns(Map.of(KEY, VALUE), MetricData::getProperties)
            .returns(MetricDataType.MINUTE, MetricData::getType);
    }
}