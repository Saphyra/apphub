package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.lib.monitoring.memory.MemoryStatusReporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MonitoringEventControllerTest {
    @Mock
    private MemoryStatusReporter memoryStatusReporter;

    @Mock
    private CollectedMetricSender collectedMetricSender;

    @InjectMocks
    private MonitoringEventController underTest;

    @Test
    void reportMemoryStatus() {
        underTest.reportMemoryStatus();

        then(memoryStatusReporter).should().reportMemoryStatus();
    }

    @Test
    void sendCollectedMetrics() {
        underTest.sendCollectedMetrics();

        then(collectedMetricSender).should().sendCollectedMetrics();
    }
}