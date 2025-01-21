package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReportingEventControllerTest {
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private PerformanceReportingTopicStatusCache performanceReportingTopicStatusCache;

    @InjectMocks
    private PerformanceReportingEventController underTest;

    @Test
    void topicStatusModifiedEvent() {
        SendEventRequest<PerformanceReportingTopicStatus> event = SendEventRequest.<PerformanceReportingTopicStatus>builder()
            .payload(PerformanceReportingTopicStatus.builder()
                .topic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)
                .expiration(EXPIRATION)
                .build())
            .build();

        underTest.topicStatusModifiedEvent(event);

        then(performanceReportingTopicStatusCache).should().update(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, EXPIRATION);
    }
}