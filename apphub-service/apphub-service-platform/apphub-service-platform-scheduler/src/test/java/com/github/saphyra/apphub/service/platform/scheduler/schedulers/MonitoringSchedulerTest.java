package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MonitoringSchedulerTest {
    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @Mock
    private SleepService sleepService;

    @InjectMocks
    private MonitoringScheduler underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<?>> argumentCaptor;

    @Test
    void aggregateSecondMetrics() {
        underTest.aggregateSecondMetrics();

        verifyEventSent(MonitoringEvent.AGGREGATE_SECOND_METRICS);
    }

    @Test
    void aggregateMinuteMetrics() {
        underTest.aggregateMinuteMetrics();

        verifyEventSent(MonitoringEvent.AGGREGATE_MINUTE_METRICS);
    }

    @Test
    void deleteExpiredMetrics() {
        underTest.deleteExpiredMetrics();

        verifyEventSent(MonitoringEvent.DELETE_EXPIRED_METRICS);
    }

    @Test
    void reportMemoryStatus() {
        underTest.reportMemoryStatus();

        verifyEventSent(MonitoringEvent.REPORT_MEMORY_STATUS);
    }

    @Test
    void reportInMemoryDaoStatus() {
        underTest.reportInMemoryDaoStatus();

        verifyEventSent(MonitoringEvent.REPORT_IN_MEMORY_DAO_STATUS);
    }

    @Test
    void sendCollectedMetrics() {
        underTest.sendCollectedMetrics();

        then(sleepService).should().sleep(500);

        verifyEventSent(MonitoringEvent.SEND_COLLECTED_METRICS);
    }

    private void verifyEventSent(String event) {
        then(eventGatewayApiClient).should().sendEvent(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(event);
    }
}