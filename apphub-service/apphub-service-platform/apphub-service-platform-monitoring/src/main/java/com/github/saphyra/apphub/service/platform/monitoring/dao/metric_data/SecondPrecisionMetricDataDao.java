package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
//TODO unit test
class SecondPrecisionMetricDataDao implements MetricDataDao {
    private final Map<UUID, MetricData> repository = new ConcurrentHashMap<>();

    @Override
    public void deleteByTimestampBefore(LocalDateTime expiration) {
        repository.values().removeIf(metricData -> metricData.getTimestamp().isBefore(expiration));
    }

    @Override
    public Set<String> getServices() {
        return repository.values()
            .stream()
            .map(MetricData::getService)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<UUID> getMetricIds() {
        return repository.values()
            .stream()
            .map(MetricData::getMetricId)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<MetricData> findOldest(MetricDataType metricDataType) {
        return repository.values()
            .stream()
            .max(Comparator.comparing(MetricData::getTimestamp));
    }

    @Override
    public Collection<MetricData> getByTypeBetween(MetricDataType metricDataType, LocalDateTime expirationStart, LocalDateTime expirationEnd) {
        return repository.values()
            .stream()
            .filter(metricData -> !metricData.getTimestamp().isBefore(expirationStart) && !metricData.getTimestamp().isAfter(expirationEnd))
            .toList();
    }

    @Override
    public void save(MetricData metricData) {
        repository.put(metricData.getMetricDataId(), metricData);
    }

    @Override
    public void deleteAll(List<MetricData> metricData) {
        metricData.forEach(data -> repository.remove(data.getMetricDataId()));
    }

    @Override
    public Collection<MetricData> getByTypeAndMetricIdInAndServiceAfter(MetricDataType type, Collection<UUID> metricIds, @Nullable String service, LocalDateTime timestamp) {
        return repository.values()
            .stream()
            .filter(metricData -> metricData.getType() == type)
            .filter(metricData -> metricIds.contains(metricData.getMetricId()))
            .filter(metricData -> service == null || service.equals(metricData.getService()))
            .filter(metricData -> !metricData.getTimestamp().isBefore(timestamp))
            .collect(Collectors.toList());
    }
}
