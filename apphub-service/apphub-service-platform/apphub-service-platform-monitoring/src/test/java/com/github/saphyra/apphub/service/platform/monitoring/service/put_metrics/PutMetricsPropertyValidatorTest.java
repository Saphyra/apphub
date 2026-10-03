package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PutMetricsPropertyValidatorTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String KEY = "key";
    private static final String KEY_2 = "key-2";

    @Mock
    private MetricPropertyDao metricPropertyDao;

    @Mock
    private MetricPropertyFactory metricPropertyFactory;

    @InjectMocks
    private PutMetricsPropertyValidator underTest;

    @Mock
    private MetricPropertyModel propertyModel;

    @Mock
    private MetricPropertyModel propertyModel2;

    @Mock
    private MetricProperty metricProperty;

    @Mock
    private MetricProperty metricProperty2;

    @Test
    void createNew() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of());
        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(metricPropertyFactory.create(METRIC_ID, KEY, AggregationStrategy.SUM)).willReturn(metricProperty);

        underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel));

        then(metricPropertyDao).should().save(metricProperty);
    }

    @Test
    void missingProperty() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty, metricProperty2));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(metricProperty2.getProperty()).willReturn(KEY_2);
        given(metricProperty2.getAggregationStrategy()).willReturn(AggregationStrategy.AVERAGE);

        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        ExceptionValidator.validateReportedException(
            catchThrowable(() -> underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel))),
            HttpStatus.BAD_REQUEST,
            ErrorCode.GENERAL_ERROR
        );
    }

    @Test
    void newPropertySaved() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(propertyModel2.getKey()).willReturn(KEY_2);
        given(propertyModel2.getAggregationStrategy()).willReturn(AggregationStrategy.AVERAGE);
        given(metricPropertyFactory.create(METRIC_ID, KEY_2, AggregationStrategy.AVERAGE)).willReturn(metricProperty2);

        underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel, propertyModel2));

        then(metricPropertyDao).should().save(metricProperty2);
    }

    @Test
    void differentAggregationStrategy() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.AVERAGE);

        ExceptionValidator.validateReportedException(
            catchThrowable(() -> underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel))),
            HttpStatus.BAD_REQUEST,
            ErrorCode.GENERAL_ERROR
        );
    }

    @Test
    void differentProperty() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        given(propertyModel.getKey()).willReturn("asd");
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        ExceptionValidator.validateReportedException(
            catchThrowable(() -> underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel))),
            HttpStatus.BAD_REQUEST,
            ErrorCode.GENERAL_ERROR
        );
    }

    @Test
    void validModel() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        underTest.saveOrVerifyProperties(METRIC_ID, List.of(propertyModel));

        then(metricPropertyDao).should(times(0)).save(any());
    }
}