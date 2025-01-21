package com.github.saphyra.apphub.service.admin_panel.performance_reporting.service;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.PerformanceReportingProperties;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceReportingTopicStatusService {
    private static final Map<PerformanceReportingTopic, LocalDateTime> DAO = new ConcurrentHashMap<>();

    private final DateTimeUtil dateTimeUtil;
    private final EventGatewayProxy eventGatewayProxy;
    private final PerformanceReportingProperties properties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;

    public void enable(PerformanceReportingTopic topic) {
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .plus(properties.getTopicExpiration());

        DAO.put(topic, expiration);

        scheduledExecutorServiceBean.schedule(() -> removeIfExpired(topic), properties.getTopicExpiration().plus(1, ChronoUnit.SECONDS));

        sendEvent(topic);
    }

    public void disable(PerformanceReportingTopic topic) {
        DAO.remove(topic);

        sendEvent(topic);
    }

    public Map<PerformanceReportingTopic, LocalDateTime> getAll() {
        return Arrays.stream(PerformanceReportingTopic.values())
            .collect(Collectors.toMap(Function.identity(), topic -> Optional.ofNullable(DAO.get(topic)).orElse(LocalDateTime.MIN)));
    }

    private void sendEvent(PerformanceReportingTopic topic) {
        LocalDateTime expiration = findByTopic(topic);
        SendEventRequest<PerformanceReportingTopicStatus> event = SendEventRequest.<PerformanceReportingTopicStatus>builder()
            .eventName(PerformanceReportingTopicStatus.EVENT_NAME)
            .payload(PerformanceReportingTopicStatus.builder()
                .topic(topic)
                .expiration(expiration)
                .enabled(expiration.isAfter(dateTimeUtil.getCurrentDateTime()))
                .build())
            .build();

        eventGatewayProxy.sendEvent(event);
    }

    private LocalDateTime findByTopic(PerformanceReportingTopic topic) {
        return Optional.ofNullable(DAO.get(topic))
            .orElse(LocalDateTime.MIN);
    }

    private void removeIfExpired(PerformanceReportingTopic topic) {
        Optional.ofNullable(DAO.get(topic))
            .ifPresent(expiration -> {
                if (expiration.isAfter(dateTimeUtil.getCurrentDateTime())) {
                    disable(topic);
                }
            });
    }
}
