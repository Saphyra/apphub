package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MinuteMetricAggregationDataProviderTest {
    private static final Duration EXPIRATION_DURATION = Duration.ofSeconds(10);
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration STEP_DURATION = Duration.ofSeconds(2);

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private MonitoringProperties monitoringProperties;

    @InjectMocks
    private MinuteMetricAggregationDataProvider underTest;

    @Mock
    private MonitoringProperties.Aggregation aggregation;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(MetricDataType.MINUTE);
    }

    @Test
    void getExpirationTime() {
        given(monitoringProperties.getAggregation()).willReturn(Map.of(MetricDataType.MINUTE, aggregation));
        given(aggregation.getExpirationDuration()).willReturn(EXPIRATION_DURATION);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);


        assertThat(underTest.getExpirationTime()).isEqualTo(CURRENT_TIME.withNano(0).withSecond(0).withMinute(0).minus(EXPIRATION_DURATION));
    }

    @Test
    void step() {
        given(monitoringProperties.getAggregation()).willReturn(Map.of(MetricDataType.MINUTE, aggregation));
        given(aggregation.getStepDuration()).willReturn(STEP_DURATION);

        assertThat(underTest.step(CURRENT_TIME)).isEqualTo(CURRENT_TIME.minus(STEP_DURATION));
    }

    @Test
    void getResultType() {
        assertThat(underTest.getResultType()).isEqualTo(MetricDataType.HOUR);
    }
}