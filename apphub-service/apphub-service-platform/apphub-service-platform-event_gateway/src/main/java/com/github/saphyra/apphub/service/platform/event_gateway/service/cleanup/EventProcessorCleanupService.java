package com.github.saphyra.apphub.service.platform.event_gateway.service.cleanup;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@Slf4j
class EventProcessorCleanupService {
    private final EventProcessorDao eventProcessorDao;
    private final DateTimeUtil dateTimeUtil;
    private final Integer eventProcessorExpirationSeconds;

    @Builder
    EventProcessorCleanupService(
        EventProcessorDao eventProcessorDao,
        DateTimeUtil dateTimeUtil,
        @Value("${eventProcessor.cleanup.expirationSeconds}") Integer eventProcessorExpirationSeconds
    ) {
        this.eventProcessorDao = eventProcessorDao;
        this.dateTimeUtil = dateTimeUtil;
        this.eventProcessorExpirationSeconds = eventProcessorExpirationSeconds;
    }

    @Scheduled(fixedRateString = "${eventProcessor.cleanup.interval}")
    void cleanupExpiredEventProcessors() {
        log.info("Cleaning up expired eventProcessors...");
        LocalDateTime expiration = dateTimeUtil.getCurrentDate()
            .minusSeconds(eventProcessorExpirationSeconds);
        List<EventProcessor> eventProcessors = eventProcessorDao.getByLastAccessBefore(expiration);
        eventProcessorDao.deleteAll(eventProcessors);
        log.info("Number of expired eventProcessors: {}", eventProcessors.size());
    }
}
