package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReporterTest {
    private static final String KEY = "key";

    @Mock
    private PerformanceReportSender performanceReportSender;

    @InjectMocks
    private PerformanceReporter underTest;

    @Mock
    private Runnable task;

    @Test
    void wrap() {
        underTest.wrap(task, PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY);

        then(task).should().run();
        then(performanceReportSender).should().sendReport(eq(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), eq(KEY), anyLong());
    }
}