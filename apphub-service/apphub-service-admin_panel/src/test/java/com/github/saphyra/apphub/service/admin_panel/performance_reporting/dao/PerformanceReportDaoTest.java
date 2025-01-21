package com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.PerformanceReportingProperties;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PerformanceReportDaoTest {
    private static final String KEY = "key";
    private static final Long VALUE = 345L;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration REPORT_EXPIRATION = Duration.ofSeconds(2);

    @Mock
    private PerformanceReportingProperties properties;

    @Spy
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean = ExecutorServiceBeenTestUtils.createFactory(mock(ErrorReporterService.class)).createScheduled(1);

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private PerformanceReportDao underTest;

    @Test
    void addAndGetByTopic() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getReportExpiration()).willReturn(REPORT_EXPIRATION);

        underTest.add(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        CustomAssertions.singleListAssertThat(underTest.getByTopic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING))
            .returns(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReport::getTopic)
            .returns(KEY, PerformanceReport::getKey)
            .returns(VALUE, PerformanceReport::getValue)
            .returns(CURRENT_TIME, PerformanceReport::getCreatedAt);
    }

    @Test
    void cleanup() throws InterruptedException {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getReportExpiration()).willReturn(REPORT_EXPIRATION);
        given(properties.getCleanupRateSeconds()).willReturn(1L);

        underTest.add(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        underTest.initiateCleanup();

        assertThat(underTest.getByTopic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).hasSize(1);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME.plusSeconds(3));

        Thread.sleep(2500);
        assertThat(underTest.getByTopic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).isEmpty();
    }
}