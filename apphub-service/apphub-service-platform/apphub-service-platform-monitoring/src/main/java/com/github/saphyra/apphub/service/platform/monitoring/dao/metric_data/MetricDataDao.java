package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class MetricDataDao extends AbstractDao<MetricDataEntity, MetricData, String, MetricDataRepository> {
    private final UuidConverter uuidConverter;

    MetricDataDao(MetricDataConverter converter, MetricDataRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByTimestampBefore(LocalDateTime expiration) {
        repository.deleteByExpirationBefore(expiration);
    }

    public List<MetricData> getByTypeBetween(com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType type, LocalDateTime expirationStart, LocalDateTime expirationEnd) {
        return converter.convertEntity(repository.getByTypeBetween(type, expirationStart, expirationEnd));
    }

    public List<MetricData> getByTypeAndMetricIdInAndServiceAfter(MetricDataType type, List<UUID> metricIds, @Nullable String service, LocalDateTime timestamp) {
        return converter.convertEntity(repository.getByTypeAndMetricIdInAndServiceAfter(type, uuidConverter.convertDomain(metricIds), service, timestamp));
    }

    public Optional<MetricData> findOldest(MetricDataType metricDataType) {
        return converter.convertEntity(repository.findFirstByTypeOrderByTimestampAsc(metricDataType));
    }
}
