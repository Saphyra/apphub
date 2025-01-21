package com.github.saphyra.apphub.service.admin_panel.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.admin_panel.server.PerformanceReportingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao.PerformanceReport;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao.PerformanceReportDao;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.service.PerformanceReportingTopicStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
class PerformanceReportingControllerImpl implements PerformanceReportingController {
    private final PerformanceReportingTopicStatusService performanceReportingTopicStatusService;
    private final PerformanceReportDao performanceReportDao;
    private final DateTimeUtil dateTimeUtil;

    @Override
    public List<PerformanceReportingTopicStatus> getTopicStatus(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the status of the topics.", Optional.ofNullable(accessTokenHeader).map(AccessTokenHeader::getUserId).map(UUID::toString).orElse("A service"));

        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();

        return performanceReportingTopicStatusService.getAll()
            .entrySet()
            .stream()
            .map(entry -> PerformanceReportingTopicStatus.builder()
                .topic(entry.getKey())
                .expiration(entry.getValue())
                .enabled(entry.getValue().isAfter(currentTime))
                .build())
            .toList();
    }

    @Override
    public List<PerformanceReportingTopicStatus> enableTopic(OneParamRequest<PerformanceReportingTopic> topic, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to enable topic {}", accessTokenHeader.getUserId(), topic.getValue());

        ValidationUtil.notNull(topic.getValue(), "topic");

        performanceReportingTopicStatusService.enable(topic.getValue());

        return getTopicStatus(accessTokenHeader);
    }

    @Override
    public List<PerformanceReportingTopicStatus> disableTopic(OneParamRequest<PerformanceReportingTopic> topic, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to disable topic {}", accessTokenHeader.getUserId(), topic.getValue());

        ValidationUtil.notNull(topic.getValue(), "topic");

        performanceReportingTopicStatusService.disable(topic.getValue());

        return getTopicStatus(accessTokenHeader);
    }

    @Override
    public List<PerformanceReportResponse> getReports(PerformanceReportingTopic topic, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the reports of topic {}", accessTokenHeader.getUserId(), topic);

        return performanceReportDao.getByTopic(topic)
            .stream()
            .collect(Collectors.groupingBy(PerformanceReport::getKey))
            .entrySet()
            .stream()
            .map(entry -> PerformanceReportResponse.builder()
                .key(entry.getKey())
                .min(entry.getValue().stream().mapToLong(PerformanceReport::getValue).min().orElse(0L))
                .max(entry.getValue().stream().mapToLong(PerformanceReport::getValue).max().orElse(0L))
                .average(entry.getValue().stream().mapToLong(PerformanceReport::getValue).average().orElse(0))
                .count(entry.getValue().size())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void report(PerformanceReportRequest request) {
        performanceReportDao.add(request.getTopic(), request.getKey(), request.getValue());
    }
}
