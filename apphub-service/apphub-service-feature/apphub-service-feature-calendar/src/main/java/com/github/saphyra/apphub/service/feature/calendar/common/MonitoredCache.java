package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_ACCESS_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_HIT_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_MISS_COUNT;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class MonitoredCache<K, V> {
    protected static final String EVENT_CACHE_NAME = "eventCache";
    protected static final String EVENT_LABEL_MAPPING_CACHE_NAME = "eventLabelMappingCache";
    protected static final String LABEL_CACHE_NAME = "labelCache";
    protected static final String LABEL_EVENT_MAPPING_CACHE_NAME = "labelEventMappingCache";
    protected static final String OCCURRENCE_CACHE_NAME = "occurrenceCache";
    protected static final String PRINCIPAL_ALM_CACHE_NAME = "principalAlmCache";
    protected static final String OBJECT_ALM_CACHE_NAME = "objectAlmCache";

    protected final String cacheName;
    protected final Cache<K, V> cache;
    protected final MetricRegistry metricRegistry;

    public V get(K key, Supplier<V> loader) {
        @Nullable V cached = cache.getIfPresent(key);

        if (isNull(cached)) {
            metricRegistry.reportMetric(Feature.CACHE, cacheName, cacheHitMetricProperties(0, 1));

            V value = loader.get();
            cache.put(key, value);

            return value;
        }

        metricRegistry.reportMetric(Feature.CACHE, cacheName, cacheHitMetricProperties(1, 0));
        return cached;
    }

    public void invalidate(K userId) {
        cache.invalidate(userId);
    }

    public void invalidateAll(Collection<K> keys) {
        cache.invalidateAll(keys);
    }

    private static List<MetricPropertyModel> cacheHitMetricProperties(double cacheHitCount, double cacheMissCount) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_CACHE_ACCESS_COUNT)
                .value(1d)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_CACHE_HIT_COUNT)
                .value(cacheHitCount)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_CACHE_MISS_COUNT)
                .value(cacheMissCount)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build()
        );
    }
}
