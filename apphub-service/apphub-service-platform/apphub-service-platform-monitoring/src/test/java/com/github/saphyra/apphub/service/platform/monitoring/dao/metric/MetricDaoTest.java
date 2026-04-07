package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
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
class MetricDaoTest {
    private static final String METRIC_ID_STRING = "metric-id";
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String FUNCTIONALITY = "functionality";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private MetricFactory metricFactory;

    @Mock
    private MetricConverter converter;

    @Mock
    private MetricRepository repository;

    @InjectMocks
    private MetricDao underTest;

    @Mock
    private Metric domain;

    @Mock
    private MetricEntity entity;

    @Test
    void extractId() {
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);

        assertThat(underTest.extractId(domain)).isEqualTo(METRIC_ID_STRING);
    }

    @Test
    void findOrCreate_existing() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getFunctionality()).willReturn(FUNCTIONALITY);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.findOrCreate(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).isEqualTo(domain);
    }

    @Test
    void findOrCreate_createNew() throws NoSuchFieldException, IllegalAccessException {
        given(metricFactory.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).willReturn(domain);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(converter.convertDomain(domain)).willReturn(entity);

        assertThat(underTest.findOrCreate(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).isEqualTo(domain);

        then(repository).should().save(entity);
        assertThat(ReflectionUtils.<Map<String, Metric>>getFieldValue(underTest, "cache")).containsEntry(METRIC_ID_STRING, domain);
    }

    @Test
    void getFeatures() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.getFeatures()).containsExactly(Feature.ELITE_BASE_MESSAGE_PROCESSING);
    }

    @Test
    void getFunctionalitiesOfFeature() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFunctionality()).willReturn(FUNCTIONALITY);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.getFunctionalitiesOfFeature(Feature.ELITE_BASE_MESSAGE_PROCESSING)).containsExactly(FUNCTIONALITY);
    }

    @Test
    void findByFeatureAndFunctionalityValidated() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFunctionality()).willReturn(FUNCTIONALITY);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.findByFeatureAndFunctionalityValidated(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).isEqualTo(domain);
    }

    @Test
    void getByFeatureAndOptionalFunctionality() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFunctionality()).willReturn(FUNCTIONALITY);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.getByFeatureAndOptionalFunctionality(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).containsExactly(domain);
    }

    @Test
    void getByFeatureAndOptionalFunctionality_nullFunctionality() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();

        assertThat(underTest.getByFeatureAndOptionalFunctionality(Feature.ELITE_BASE_MESSAGE_PROCESSING, null)).containsExactly(domain);
    }

    @Test
    void deleteByMetricIdNotIn() throws NoSuchFieldException, IllegalAccessException {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getMetricId()).willReturn(METRIC_ID);
        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);
        given(domain.getMetricId()).willReturn(METRIC_ID);
        underTest.findAll();
        given(uuidConverter.convertDomain(List.of(METRIC_ID))).willReturn(List.of(METRIC_ID_STRING));
        given(uuidConverter.convertEntity(METRIC_ID_STRING)).willReturn(METRIC_ID);

        underTest.deleteByMetricIdNotIn(List.of(METRIC_ID));

        then(repository).should().deleteByMetricIdNotIn(List.of(METRIC_ID_STRING));
        assertThat(ReflectionUtils.<Map<?, ?>>getFieldValue(underTest, "cache")).isNotEmpty();
    }
}