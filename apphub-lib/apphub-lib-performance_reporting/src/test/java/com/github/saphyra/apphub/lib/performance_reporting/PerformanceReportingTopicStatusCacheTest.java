package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReportingTopicStatusCacheTest {
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private PerformanceReportingProxy performanceReportingProxy;

    @InjectMocks
    private PerformanceReportingTopicStatusCache underTest;

    @Mock
    private PerformanceReportingTopicStatus performanceReportingTopicStatus;

    @Test
    void load() {
        given(performanceReportingProxy.getTopicStatus()).willReturn(List.of(performanceReportingTopicStatus));
        given(performanceReportingTopicStatus.getTopic()).willReturn(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING);
        given(performanceReportingTopicStatus.getExpiration()).willReturn(EXPIRATION);

        assertThat(underTest.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).contains(EXPIRATION);
        assertThat(underTest.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).contains(EXPIRATION);

        then(performanceReportingProxy).should().getTopicStatus();
    }

    @Test
    void update() {
        underTest.update(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, EXPIRATION);

        assertThat(underTest.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).contains(EXPIRATION);

        then(performanceReportingProxy).shouldHaveNoInteractions();
    }
}