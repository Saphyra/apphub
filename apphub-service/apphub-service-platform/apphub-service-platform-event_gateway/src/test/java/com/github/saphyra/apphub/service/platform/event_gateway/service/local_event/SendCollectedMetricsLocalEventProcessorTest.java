package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.monitoring.core.CollectedMetricSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SendCollectedMetricsLocalEventProcessorTest {
    @Mock
    private CollectedMetricSender collectedMetricSender;

    @InjectMocks
    private SendCollectedMetricsLocalEventProcessor underTest;

    @Test
    void shouldProcess() {
        assertThat(underTest.shouldProcess(MonitoringEvent.SEND_COLLECTED_METRICS)).isTrue();
    }

    @Test
    void process() {
        SendEventRequest<?> request = SendEventRequest.builder()
            .eventName(MonitoringEvent.SEND_COLLECTED_METRICS)
            .build();

        underTest.process(request);

        then(collectedMetricSender).should().sendCollectedMetrics();
    }
}