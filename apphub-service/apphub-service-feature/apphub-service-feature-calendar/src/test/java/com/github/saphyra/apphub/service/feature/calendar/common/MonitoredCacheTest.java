package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_ACCESS_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_HIT_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_CACHE_MISS_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MonitoredCacheTest {
    private static final String CACHE_NAME = "cache-name";
    private static final String VALUE = "value";
    private static final String KEY = "key";

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Cache<String, String> cache;

    private TestCache underTest;

    @Mock
    private Supplier<String> loader;

    @Test
    void get() {
        underTest = new TestCache(metricRegistry);

        given(loader.get()).willReturn(VALUE);

        assertThat(underTest.get(KEY, loader)).isEqualTo(VALUE);
        assertThat(underTest.get(KEY, loader)).isEqualTo(VALUE);

        then(loader).should(times(1)).get();

        ArgumentCaptor<List<MetricPropertyModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        then(metricRegistry).should(times(2)).reportMetric(eq(Feature.CACHE), eq(CACHE_NAME), argumentCaptor.capture());

        assertThat(argumentCaptor.getAllValues().getFirst())
            .extracting(MetricPropertyModel::getKey, MetricPropertyModel::getValue, MetricPropertyModel::getAggregationStrategy)
            .containsExactlyInAnyOrder(
                new Tuple(KEY_CACHE_ACCESS_COUNT, 1d, AggregationStrategy.SUM),
                new Tuple(KEY_CACHE_HIT_COUNT, 0d, AggregationStrategy.SUM),
                new Tuple(KEY_CACHE_MISS_COUNT, 1d, AggregationStrategy.SUM)
            );

        assertThat(argumentCaptor.getAllValues().get(1))
            .extracting(MetricPropertyModel::getKey, MetricPropertyModel::getValue, MetricPropertyModel::getAggregationStrategy)
            .containsExactlyInAnyOrder(
                new Tuple(KEY_CACHE_ACCESS_COUNT, 1d, AggregationStrategy.SUM),
                new Tuple(KEY_CACHE_HIT_COUNT, 1d, AggregationStrategy.SUM),
                new Tuple(KEY_CACHE_MISS_COUNT, 0d, AggregationStrategy.SUM)
            );
    }

    @Test
    void invalidate() {
        underTest = new TestCache(cache, metricRegistry);

        underTest.invalidate(KEY);

        then(cache).should().invalidate(KEY);
    }

    @Test
    void invalidateAll() {
        underTest = new TestCache(cache, metricRegistry);

        underTest.invalidateAll(List.of(KEY));

        then(cache).should().invalidateAll(List.of(KEY));
    }

    private static class TestCache extends MonitoredCache<String, String> {
        public TestCache(Cache<String, String> cache, MetricRegistry metricRegistry) {
            super(CACHE_NAME, cache, metricRegistry);
        }

        public TestCache(MetricRegistry metricRegistry) {
            super(CACHE_NAME, Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build(), metricRegistry);
        }
    }
}