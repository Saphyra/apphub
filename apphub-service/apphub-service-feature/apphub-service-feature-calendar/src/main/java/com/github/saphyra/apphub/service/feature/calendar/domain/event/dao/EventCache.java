package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import com.github.saphyra.apphub.service.feature.calendar.common.MonitoredCache;
import com.github.saphyra.apphub.service.feature.calendar.config.CalendarProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Cache<UserId, Map<EventId, Event>>
 */
@Component
class EventCache extends MonitoredCache<UUID, Map<UUID, Event>> {
    EventCache(CalendarProperties properties, MetricRegistry metricRegistry) {
        super(EVENT_CACHE_NAME, buildCache(properties.getCacheExpirationDuration()), metricRegistry);
    }

    private static Cache<UUID, Map<UUID, Event>> buildCache(Duration expiration) {
        return Caffeine.newBuilder()
            .expireAfterAccess(expiration)
            .build();
    }
}
