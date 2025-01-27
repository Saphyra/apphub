package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReportSenderTest {
    private static final String KEY = "key";
    private static final long VALUE = 432L;
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private PerformanceReportingTopicStatusCache performanceReportingTopicStatusCache;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PerformanceReportingProxy performanceReportingProxy;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private PerformanceReportSender underTest;

    @BeforeEach
    void setUp() {
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        });
    }

    @Test
    void error() {
        given(performanceReportingTopicStatusCache.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).willThrow(new RuntimeException());

        underTest.sendReport(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        then(performanceReportingProxy).shouldHaveNoInteractions();
    }

    @Test
    void topicDisabled() {
        given(performanceReportingTopicStatusCache.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).willReturn(Optional.of(EXPIRATION));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION.plusSeconds(1));

        underTest.sendReport(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        then(performanceReportingProxy).shouldHaveNoInteractions();
    }

    @Test
    void sendReport() {
        given(performanceReportingTopicStatusCache.get(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).willReturn(Optional.of(EXPIRATION));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION.minusSeconds(1));

        underTest.sendReport(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        then(performanceReportingProxy).should().report(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);
    }
}