package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

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
class MetricPropertyDaoTest {
    private static final UUID METRIC_ID_1 = UUID.randomUUID();
    private static final UUID METRIC_ID_2 = UUID.randomUUID();
    private static final String PROPERTY = "property";
    private static final String METRIC_ID_STRING = "metric-id";

    @Mock
    private MetricPropertyConverter converter;

    @Mock
    private MetricPropertyRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricPropertyDao underTest;

    @Mock
    private MetricProperty domain;

    @Mock
    private MetricPropertyEntity entity;

    @Test
    void extractId() {
        given(domain.getMetricId()).willReturn(METRIC_ID_1);
        given(domain.getProperty()).willReturn(PROPERTY);
        given(uuidConverter.convertDomain(METRIC_ID_1)).willReturn(METRIC_ID_STRING);

        assertThat(underTest.extractId(domain))
            .returns(METRIC_ID_STRING, MetricPropertyId::getMetricId)
            .returns(PROPERTY, MetricPropertyId::getProperty);
    }

    @Test
    void getByMetricId() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID_1);
        given(domain.getProperty()).willReturn(PROPERTY);
        given(uuidConverter.convertDomain(METRIC_ID_1)).willReturn(METRIC_ID_STRING);
        underTest.findAll();

        assertThat(underTest.getByMetricId(METRIC_ID_1)).containsExactly(domain);
    }

    @Test
    void deleteByMetricIdsNotIn() throws NoSuchFieldException, IllegalAccessException {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID_2);
        given(domain.getProperty()).willReturn(PROPERTY);
        given(uuidConverter.convertDomain(METRIC_ID_2)).willReturn(METRIC_ID_STRING);
        underTest.findAll();
        given(uuidConverter.convertDomain(List.of(METRIC_ID_1))).willReturn(List.of(METRIC_ID_STRING));

        underTest.deleteByMetricIdsNotIn(List.of(METRIC_ID_1));

        assertThat(ReflectionUtils.<Map<?, ?>>getFieldValue(underTest, "cache")).isEmpty();
        then(repository).should().deleteByMetricIdsNotIn(List.of(METRIC_ID_STRING));
    }
}