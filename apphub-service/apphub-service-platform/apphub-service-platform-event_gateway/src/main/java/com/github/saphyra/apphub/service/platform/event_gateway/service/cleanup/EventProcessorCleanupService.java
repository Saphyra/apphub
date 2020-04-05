package com.github.saphyra.apphub.service.platform.event_gateway.service.cleanup;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@EnableScheduling
@Slf4j
class EventProcessorCleanupService {
    private final EventProcessorDao eventProcessorDao;
    private final OffsetDateTimeProvider offsetDateTimeProvider;
    private final Integer eventProcessorExpirationSeconds;

    @Builder
    EventProcessorCleanupService(
        EventProcessorDao eventProcessorDao,
        OffsetDateTimeProvider offsetDateTimeProvider,
        @Value("${eventProcessor.cleanup.expirationSeconds}") Integer eventProcessorExpirationSeconds
    ) {
        this.eventProcessorDao = eventProcessorDao;
        this.offsetDateTimeProvider = offsetDateTimeProvider;
        this.eventProcessorExpirationSeconds = eventProcessorExpirationSeconds;
    }

    @Scheduled(fixedRateString = "${eventProcessor.cleanup.interval}")
    void cleanupExpiredEventProcessors() {
        log.info("Cleaning up expired eventProcessors...");
        OffsetDateTime expiration = offsetDateTimeProvider.getCurrentDate()
            .minusSeconds(eventProcessorExpirationSeconds);
        List<EventProcessor> eventProcessors = eventProcessorDao.getByLastAccessBefore(expiration);
        eventProcessorDao.deleteAll(eventProcessors);
        log.info("Number of expired eventProcessors: {}", eventProcessors.size());
    }
}
