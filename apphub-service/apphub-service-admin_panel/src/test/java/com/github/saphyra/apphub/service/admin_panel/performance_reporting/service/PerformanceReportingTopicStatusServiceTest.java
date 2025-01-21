package com.github.saphyra.apphub.service.admin_panel.performance_reporting.service;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.PerformanceReportingProperties;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PerformanceReportingTopicStatusServiceTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration EXPIRATION = Duration.ofSeconds(2);

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EventGatewayProxy eventGatewayProxy;

    @Mock
    private PerformanceReportingProperties properties;

    @Spy
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean = ExecutorServiceBeenTestUtils.createFactory(mock(ErrorReporterService.class)).createScheduled(1);

    @InjectMocks
    private PerformanceReportingTopicStatusService underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<PerformanceReportingTopicStatus>> argumentCaptor;

    @Test
    void enableAndExpire() throws InterruptedException {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getTopicExpiration()).willReturn(EXPIRATION);

        underTest.enable(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING);

        then(eventGatewayProxy).should().sendEvent(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .returns(PerformanceReportingTopicStatus.EVENT_NAME, SendEventRequest::getEventName)
            .extracting(SendEventRequest::getPayload)
            .returns(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportingTopicStatus::getTopic)
            .returns(CURRENT_TIME.plus(EXPIRATION), PerformanceReportingTopicStatus::getExpiration)
            .returns(true, PerformanceReportingTopicStatus::getEnabled);

        assertThat(underTest.getAll()).containsEntry(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, CURRENT_TIME.plus(EXPIRATION));

        Thread.sleep(3500);

        assertThat(underTest.getAll()).containsEntry(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, LocalDateTime.MIN);
    }

    @Test
    void disable() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.disable(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING);

        then(eventGatewayProxy).should().sendEvent(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .returns(PerformanceReportingTopicStatus.EVENT_NAME, SendEventRequest::getEventName)
            .extracting(SendEventRequest::getPayload)
            .returns(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportingTopicStatus::getTopic)
            .returns(LocalDateTime.MIN, PerformanceReportingTopicStatus::getExpiration)
            .returns(false, PerformanceReportingTopicStatus::getEnabled);
    }
}