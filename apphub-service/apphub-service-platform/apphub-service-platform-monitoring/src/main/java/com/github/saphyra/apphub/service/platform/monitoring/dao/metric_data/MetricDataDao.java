package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
//TODO unit test
public class MetricDataDao extends AbstractDao<MetricDataEntity, MetricData, String, MetricDataRepository> {
    MetricDataDao(MetricDataConverter converter, MetricDataRepository repository) {
        super(converter, repository);
    }

    public void deleteByTimestampBefore(LocalDateTime expiration) {
        repository.deleteByExpirationBefore(expiration);
    }

    public List<MetricData> getByTypeBetween(MetricDataType type, LocalDateTime expirationStart, LocalDateTime expirationEnd) {
        return converter.convertEntity(repository.getByTypeBetween(type, expirationStart, expirationEnd));
    }
}
