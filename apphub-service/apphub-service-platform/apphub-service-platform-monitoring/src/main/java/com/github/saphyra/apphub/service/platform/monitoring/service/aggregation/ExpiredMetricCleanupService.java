package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredMetricCleanupService {
    private final MetricDataDao metricDataDao;
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringProperties monitoringProperties;
    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricPropertyDao metricPropertyDao;

    @Transactional
    public void cleanup() {
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(monitoringProperties.getAggregation().get(MetricDataType.HOUR).getExpirationDuration());
        log.info("Deleting metrics before {}", expiration);

        metricDataDao.deleteByTimestampBefore(expiration);

        deleteByMetricId();
        deleteByService();
    }

    private void deleteByService() {
        List<String> services = metricDataDao.getServices();
        metricServiceDao.deleteByServiceNotIn(services);
    }

    private void deleteByMetricId() {
        List<UUID> metricIds = metricDataDao.getMetricIds();

        metricDao.deleteByMetricIdNotIn(metricIds);
        metricServiceDao.deleteByMetricIdsNotIn(metricIds);
        metricPropertyDao.deleteByMetricIdsNotIn(metricIds);
    }
}
