package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class MetricDataDao extends AbstractDao<MetricDataEntity, MetricData, String, MetricDataRepository> {
    MetricDataDao(MetricDataConverter converter, MetricDataRepository repository) {
        super(converter, repository);
    }

    public void deleteByTimestampBefore(LocalDateTime expiration) {
        repository.deleteByExpirationBefore(expiration);
    }

    public List<MetricData> getByTypeBetween(com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType type, LocalDateTime expirationStart, LocalDateTime expirationEnd) {
        return converter.convertEntity(repository.getByTypeBetween(type, expirationStart, expirationEnd));
    }

    public List<MetricData> getByTypeAndMetricIdInAndService(MetricDataType type, List<UUID> metricIds, @Nullable String service) {
        return converter.convertEntity(repository.getByTypeAndMetricIdInAndService(type, metricIds, service));
    }
}
