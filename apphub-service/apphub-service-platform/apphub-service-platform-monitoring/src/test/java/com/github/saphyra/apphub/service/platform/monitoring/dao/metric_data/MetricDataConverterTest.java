package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricDataConverterTest {
    private static final UUID METRIC_DATA_ID = UUID.randomUUID();
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String KEY = "key";
    private static final Double VALUE = 23.23;
    private static final Map<String, Double> PROPERTIES = Map.of(KEY, VALUE);
    private static final String METRIC_DATA_ID_STRING = "metric-data-id";
    private static final String METRIC_ID_STRING = "metric-id";
    private static final String PROPERTIES_STRING = "properties";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MetricDataConverter underTest;

    @Test
    void convertDomain() {
        MetricData domain = MetricData.builder()
            .metricDataId(METRIC_DATA_ID)
            .metricId(METRIC_ID)
            .service(SERVICE)
            .type(MetricDataType.MINUTE)
            .timestamp(TIMESTAMP)
            .properties(PROPERTIES)
            .build();

        given(uuidConverter.convertDomain(METRIC_DATA_ID)).willReturn(METRIC_DATA_ID_STRING);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(objectMapper.writeValueAsString(PROPERTIES)).willReturn(PROPERTIES_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(METRIC_DATA_ID_STRING, MetricDataEntity::getMetricDataId)
            .returns(METRIC_ID_STRING, MetricDataEntity::getMetricId)
            .returns(SERVICE, MetricDataEntity::getService)
            .returns(MetricDataType.MINUTE, MetricDataEntity::getType)
            .returns(TIMESTAMP, MetricDataEntity::getTimestamp)
            .returns(PROPERTIES_STRING, MetricDataEntity::getProperties);
    }

    @Test
    void convertEntity() {
        MetricDataEntity entity = MetricDataEntity.builder()
            .metricDataId(METRIC_DATA_ID_STRING)
            .metricId(METRIC_ID_STRING)
            .service(SERVICE)
            .type(MetricDataType.MINUTE)
            .timestamp(TIMESTAMP)
            .properties(PROPERTIES_STRING)
            .build();

        given(uuidConverter.convertEntity(METRIC_DATA_ID_STRING)).willReturn(METRIC_DATA_ID);
        given(uuidConverter.convertEntity(METRIC_ID_STRING)).willReturn(METRIC_ID);
        given(objectMapper.readValue(eq(PROPERTIES_STRING), Mockito.<TypeReference<Map<String, Double>>>any())).willReturn(PROPERTIES);

        assertThat(underTest.convertEntity(entity))
            .returns(METRIC_DATA_ID, MetricData::getMetricDataId)
            .returns(METRIC_ID, MetricData::getMetricId)
            .returns(SERVICE, MetricData::getService)
            .returns(MetricDataType.MINUTE, MetricData::getType)
            .returns(TIMESTAMP, MetricData::getTimestamp)
            .returns(PROPERTIES, MetricData::getProperties);
    }
}