package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class MetricDataDao extends AbstractDao<MetricDataEntity, MetricData, String, MetricDataRepository> {
    MetricDataDao(MetricDataConverter converter, MetricDataRepository repository) {
        super(converter, repository);
    }
}
