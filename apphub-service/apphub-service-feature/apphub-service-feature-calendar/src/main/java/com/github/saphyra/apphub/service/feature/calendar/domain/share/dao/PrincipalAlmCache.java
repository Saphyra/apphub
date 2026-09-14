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
 * Cache<Principal, Map<ObjectId, Alm>>
 */
@Component
class PrincipalAlmCache extends MonitoredCache<UUID, Map<UUID, Alm>> {
    PrincipalAlmCache(CalendarProperties properties, MetricRegistry metricRegistry) {
        super(PRINCIPAL_ALM_CACHE_NAME, buildCache(properties.getCacheExpirationDuration()), metricRegistry);
    }

    private static Cache<UUID, Map<UUID, Alm>> buildCache(Duration expiration) {
        return Caffeine.newBuilder()
            .expireAfterAccess(expiration)
            .build();
    }
}
