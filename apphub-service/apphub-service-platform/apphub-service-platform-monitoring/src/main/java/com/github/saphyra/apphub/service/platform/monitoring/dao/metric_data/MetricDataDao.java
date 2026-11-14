package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetricDataDao {
    void deleteByTimestampBefore(LocalDateTime expiration);

    Collection<String> getServices();

    Collection<UUID> getMetricIds();

    Optional<MetricData> findOldest(MetricDataType metricDataType);

    Collection<MetricData> getByTypeBetween(MetricDataType metricDataType, LocalDateTime expirationStart, LocalDateTime expirationEnd);

    void save(MetricData aggregated);

    void deleteAll(List<MetricData> metrics);

    Collection<MetricData> getByTypeAndMetricIdInAndServiceAfter(MetricDataType type, Collection<UUID> uuids, @Nullable String service, LocalDateTime timestamp);
}
