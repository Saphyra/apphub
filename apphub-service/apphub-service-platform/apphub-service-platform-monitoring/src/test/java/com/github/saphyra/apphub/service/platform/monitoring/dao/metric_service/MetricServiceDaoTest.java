package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MetricServiceDaoTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";
    private static final String METRIC_ID_STRING = "metric-id";

    @Mock
    private MetricServiceConverter converter;

    @Mock
    private MetricServiceRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricServiceDao underTest;

    @Mock
    private MetricService domain;

    @Mock
    private MetricServiceEntity entity;

    @Test
    void extractId() {
        given(converter.convertDomain(domain)).willReturn(entity);

        assertThat(underTest.extractId(domain)).isEqualTo(entity);
    }

    @Test
    void getByMetricId() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(converter.convertDomain(domain)).willReturn(entity);
        underTest.findAll();
        given(domain.getMetricId()).willReturn(METRIC_ID);

        assertThat(underTest.getByMetricId(METRIC_ID)).containsExactly(domain);
    }

    @Test
    void deleteByServiceNotIn() throws NoSuchFieldException, IllegalAccessException {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(converter.convertDomain(domain)).willReturn(entity);
        underTest.findAll();
        given(domain.getService()).willReturn("asd");

        underTest.deleteByServiceNotIn(List.of(SERVICE));

        then(repository).should().deleteByServiceNotIn(List.of(SERVICE));
        assertThat(ReflectionUtils.<Map<?, ?>>getFieldValue(underTest, "cache")).isEmpty();
    }

    @Test
    void deleteByMetricIdsNotIn() throws NoSuchFieldException, IllegalAccessException {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(converter.convertDomain(domain)).willReturn(entity);
        underTest.findAll();
        given(domain.getMetricId()).willReturn(UUID.randomUUID());
        given(uuidConverter.convertDomain(List.of(METRIC_ID))).willReturn(List.of(METRIC_ID_STRING));

        underTest.deleteByMetricIdsNotIn(List.of(METRIC_ID));

        then(repository).should().deleteByMetricIdsNotIn(List.of(METRIC_ID_STRING));
        assertThat(ReflectionUtils.<Map<?, ?>>getFieldValue(underTest, "cache")).isEmpty();
    }

    @Test
    void getByMetricIds() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(converter.convertDomain(domain)).willReturn(entity);
        underTest.findAll();
        given(domain.getMetricId()).willReturn(METRIC_ID);

        assertThat(underTest.getByMetricIds(List.of(METRIC_ID))).containsExactly(domain);
    }
}