package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryMonitoringEventController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MemoryMonitoringLocalEventProcessorTest {
    @Mock
    private MemoryMonitoringEventController memoryMonitoringEventController;

    @InjectMocks
    private MemoryMonitoringLocalEventProcessor underTest;

    @Test
    public void shouldProcess() {
        assertThat(underTest.shouldProcess(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE)).isTrue();
        assertThat(underTest.shouldProcess("asd")).isFalse();
    }

    @Test
    public void process() {
        underTest.process(null);

        verify(memoryMonitoringEventController).sendMemoryStatus();
    }
}