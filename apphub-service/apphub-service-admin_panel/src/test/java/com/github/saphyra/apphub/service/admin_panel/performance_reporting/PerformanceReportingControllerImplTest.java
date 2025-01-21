package com.github.saphyra.apphub.service.admin_panel.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao.PerformanceReport;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao.PerformanceReportDao;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.service.PerformanceReportingTopicStatusService;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReportingControllerImplTest {
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();
    private static final String KEY = "key";
    private static final Long MIN_VALUE = 2L;
    private static final Long MAX_VALUE = 6L;

    @Mock
    private PerformanceReportingTopicStatusService performanceReportingTopicStatusService;

    @Mock
    private PerformanceReportDao performanceReportDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private PerformanceReportingControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private PerformanceReport performanceReport;

    @Test
    void getTopicStatus() {
        given(performanceReportingTopicStatusService.getAll()).willReturn(Map.of(
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            EXPIRATION
        ));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION);

        CustomAssertions.singleListAssertThat(underTest.getTopicStatus(null))
            .returns(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportingTopicStatus::getTopic)
            .returns(EXPIRATION, PerformanceReportingTopicStatus::getExpiration)
            .returns(false, PerformanceReportingTopicStatus::getEnabled);
    }

    @Test
    void enableTopic_nullValue() {
        ExceptionValidator.validateInvalidParam(() -> underTest.enableTopic(new OneParamRequest<>(null), accessTokenHeader), "topic", "must not be null");
    }

    @Test
    void enableTopic() {
        given(performanceReportingTopicStatusService.getAll()).willReturn(Map.of(
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            EXPIRATION
        ));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION);

        assertThat(underTest.enableTopic(new OneParamRequest<>(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), accessTokenHeader)).hasSize(1);

        then(performanceReportingTopicStatusService).should().enable(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING);
    }

    @Test
    void disableTopic_nullValue() {
        ExceptionValidator.validateInvalidParam(() -> underTest.disableTopic(new OneParamRequest<>(null), accessTokenHeader), "topic", "must not be null");
    }

    @Test
    void disableTopic() {
        given(performanceReportingTopicStatusService.getAll()).willReturn(Map.of(
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            EXPIRATION
        ));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION);

        assertThat(underTest.disableTopic(new OneParamRequest<>(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), accessTokenHeader)).hasSize(1);

        then(performanceReportingTopicStatusService).should().disable(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING);
    }

    @Test
    void getReports() {
        given(performanceReportDao.getByTopic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)).willReturn(List.of(performanceReport, performanceReport));
        given(performanceReport.getKey()).willReturn(KEY);
        given(performanceReport.getValue())
            .willReturn(MIN_VALUE)
            .willReturn(MAX_VALUE);

        CustomAssertions.singleListAssertThat(underTest.getReports(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, accessTokenHeader))
            .returns(KEY, PerformanceReportResponse::getKey)
            .returns(MIN_VALUE, PerformanceReportResponse::getMin)
            .returns(MAX_VALUE, PerformanceReportResponse::getMax)
            .returns((double) MAX_VALUE, PerformanceReportResponse::getAverage)
            .returns(2, PerformanceReportResponse::getCount);
    }

    @Test
    void report() {
        PerformanceReportRequest request = PerformanceReportRequest.builder()
            .topic(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING)
            .key(KEY)
            .value(MIN_VALUE)
            .build();

        underTest.report(request);

        then(performanceReportDao).should().add(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, KEY, MIN_VALUE);
    }
}