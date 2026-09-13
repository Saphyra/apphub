package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

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
 * Cache<ObjectId, Map<Principal, Alm>>
 */
@Component
class ObjectAlmCache extends MonitoredCache<UUID, Map<UUID, Alm>> {
    ObjectAlmCache(CalendarProperties properties, MetricRegistry metricRegistry) {
        super(OBJECT_ALM_CACHE_NAME, buildCache(properties.getCacheExpirationDuration()), metricRegistry);
    }

    private static Cache<UUID, Map<UUID, Alm>> buildCache(Duration expiration) {
        return Caffeine.newBuilder()
            .expireAfterAccess(expiration)
            .build();
    }
}
