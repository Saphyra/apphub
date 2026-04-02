package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricRegistryTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String FUNCTIONALITY = "functionality";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PutMetricRequestFactory putMetricRequestFactory;

    @InjectMocks
    private MetricRegistry underTest;

    @Mock
    private PutMetricsRequest request;

    @Mock
    private MetricPropertyModel propertyModel;

    @Test
    void reportMetrics() throws NoSuchFieldException, IllegalAccessException {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(putMetricRequestFactory.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, CURRENT_TIME.withNano(0), List.of(propertyModel))).willReturn(request);

        underTest.reportMetric(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, List.of(propertyModel));

        assertThat(ReflectionUtils.<Map<LocalDateTime, List<PutMetricsRequest>>>getFieldValue(underTest, "registry")).containsEntry(CURRENT_TIME.withNano(0), List.of(request));
    }
}