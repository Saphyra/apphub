package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

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
 * Cache<UserId, Map<EventId, EventLabelMapping>>
 */
@Component
class EventLabelMappingCache extends MonitoredCache<UUID, Map<UUID, EventLabelMapping>> {
    public EventLabelMappingCache(CalendarProperties properties, MetricRegistry metricRegistry) {
        super(EVENT_LABEL_MAPPING_CACHE_NAME, buildCache(properties.getCacheExpirationDuration()), metricRegistry);
    }

    private static Cache<UUID, Map<UUID, EventLabelMapping>> buildCache(Duration expiration) {
        return Caffeine.newBuilder()
            .expireAfterAccess(expiration)
            .build();
    }
}
