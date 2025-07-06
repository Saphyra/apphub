package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.client.PerformanceReportingClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultPerformanceReportingProxyTest {
    private static final String LOCALE = "locale";
    private static final String KEY = "key";
    private static final long VALUE = 234L;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private PerformanceReportingClient performanceReportingClient;

    @InjectMocks
    private DefaultPerformanceReportingProxy underTest;

    @Mock
    private PerformanceReportingTopicStatus performanceReportingTopicStatus;

    @BeforeEach
    void setUp() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
    }

    @Test
    void getTopicStatus() {
        given(performanceReportingClient.getTopicStatus(LOCALE)).willReturn(List.of(performanceReportingTopicStatus));

        assertThat(underTest.getTopicStatus()).containsExactly(performanceReportingTopicStatus);
    }

    @Test
    void report() {
        underTest.report(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, VALUE);

        ArgumentCaptor<PerformanceReportRequest> argumentCaptor = ArgumentCaptor.forClass(PerformanceReportRequest.class);
        then(performanceReportingClient).should().report(argumentCaptor.capture(), eq(LOCALE));
        assertThat(argumentCaptor.getValue())
            .returns(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportRequest::getTopic)
            .returns(KEY, PerformanceReportRequest::getKey)
            .returns(VALUE, PerformanceReportRequest::getValue);
    }
}