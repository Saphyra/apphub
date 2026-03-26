package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusReporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemoryMonitoringLocalEventProcessorTest {
    @Mock
    private MemoryStatusReporter memoryStatusReporter;

    @InjectMocks
    private MemoryMonitoringLocalEventProcessor underTest;

    @Test
    public void shouldProcess() {
        assertThat(underTest.shouldProcess(MonitoringEvent.REPORT_MEMORY_STATUS)).isTrue();
        assertThat(underTest.shouldProcess("asd")).isFalse();
    }

    @Test
    public void process() {
        underTest.process(null);

        verify(memoryStatusReporter).reportMemoryStatus();
    }
}