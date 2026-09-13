package com.github.saphyra.apphub.service.feature.calendar.domain.label_event_mapping.dao;

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
 * Cache<UserId, Map<LabelId, LabelEventMapping>>
 */
@Component
class LabelEventMappingCache extends MonitoredCache<UUID, Map<UUID, LabelEventMapping>> {
    LabelEventMappingCache(CalendarProperties properties, MetricRegistry metricRegistry) {
        super(LABEL_EVENT_MAPPING_CACHE_NAME, buildCache(properties.getCacheExpirationDuration()), metricRegistry);
    }

    private static Cache<UUID, Map<UUID, LabelEventMapping>> buildCache(Duration expiration) {
        return Caffeine.newBuilder()
            .expireAfterAccess(expiration)
            .build();
    }
}
