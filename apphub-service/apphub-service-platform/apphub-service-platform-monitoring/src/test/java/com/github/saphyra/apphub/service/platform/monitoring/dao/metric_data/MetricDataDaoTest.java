package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MetricDataDaoTest {
    private static final LocalDateTime TIMESTAMP_1 = LocalDateTime.now();
    private static final LocalDateTime TIMESTAMP_2 = TIMESTAMP_1.plusSeconds(2);
    private static final String METRIC_ID_STRING = "metric-id";
    private static final String SERVICE = "service";
    private static final UUID METRIC_ID = UUID.randomUUID();

    @Mock
    private MetricDataConverter converter;

    @Mock
    private MetricDataRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricDataDao underTest;

    @Mock
    private MetricData domain;

    @Mock
    private MetricDataEntity entity;

    @Test
    void deleteByTimestampBefore() {
        underTest.deleteByTimestampBefore(TIMESTAMP_1);

        then(repository).should().deleteByExpirationBefore(TIMESTAMP_1);
    }

    @Test
    void getByTypeBetween() {
        given(repository.getByTypeBetween(MetricDataType.MINUTE, TIMESTAMP_1, TIMESTAMP_2)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByTypeBetween(MetricDataType.MINUTE, TIMESTAMP_1, TIMESTAMP_2)).containsExactly(domain);
    }

    @Test
    void getByTypeAndMetricIdInAndServiceAfter() {
        given(repository.getByTypeAndMetricIdInAndServiceAfter(MetricDataType.MINUTE, List.of(METRIC_ID_STRING), SERVICE, TIMESTAMP_1)).willReturn(List.of(entity));
        given(uuidConverter.convertDomain(List.of(METRIC_ID))).willReturn(List.of(METRIC_ID_STRING));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByTypeAndMetricIdInAndServiceAfter(MetricDataType.MINUTE, List.of(METRIC_ID), SERVICE, TIMESTAMP_1)).containsExactly(domain);
    }

    @Test
    void findOldest() {
        given(repository.findFirstByTypeOrderByTimestampAsc(MetricDataType.MINUTE)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findOldest(MetricDataType.MINUTE)).contains(domain);
    }

    @Test
    void getServices() {
        given(repository.getServices()).willReturn(List.of(SERVICE));

        assertThat(underTest.getServices()).containsExactly(SERVICE);
    }

    @Test
    void getMetricIds() {
        given(repository.getMetricIds()).willReturn(List.of(METRIC_ID_STRING));
        given(uuidConverter.convertEntity(List.of(METRIC_ID_STRING))).willReturn(List.of(METRIC_ID));

        assertThat(underTest.getMetricIds()).containsExactly(METRIC_ID);
    }
}