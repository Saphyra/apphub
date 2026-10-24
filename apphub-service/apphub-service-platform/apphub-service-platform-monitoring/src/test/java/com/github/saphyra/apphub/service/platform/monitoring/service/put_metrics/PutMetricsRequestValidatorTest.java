package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PutMetricsRequestValidatorTest {
    private static final String FUNCTIONALITY = "functionality";
    private static final MetricPropertyModel PROPERTY_MODEL = MetricPropertyModel.builder()
        .key("key")
        .value(23.2)
        .aggregationStrategy(AggregationStrategy.SUM)
        .build();
    private static final List<MetricPropertyModel> PROPERTIES = List.of(PROPERTY_MODEL);

    private final PutMetricsRequestValidator underTest = new PutMetricsRequestValidator();

    @Test
    void valid() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .properties(PROPERTIES)
            .build();

        underTest.validate(request);
    }

    @Test
    void nullFeature() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(null)
            .functionality(FUNCTIONALITY)
            .properties(PROPERTIES)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "feature", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void blankFunctionality() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(" ")
            .properties(PROPERTIES)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "functionality", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void emptyProperties() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .properties(List.of())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "properties", ValidationUtil.MUST_NOT_BE_EMPTY);
    }

    @Test
    void blankPropertyKey() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .properties(List.of(PROPERTY_MODEL.toBuilder().key(" ").build()))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "property.key", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void nullPropertyValue() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .properties(List.of(PROPERTY_MODEL.toBuilder().value(null).build()))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "property.value", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullPropertyAggregationStrategy() {
        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .properties(List.of(PROPERTY_MODEL.toBuilder().aggregationStrategy(null).build()))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "property.aggregationStrategy", ValidationUtil.MUST_NOT_BE_NULL);
    }
}