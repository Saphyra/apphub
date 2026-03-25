package com.github.saphyra.apphub.service.platform.monitoring.service.migration;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ExpiredMetricCleanupService {
    private final MetricDataDao metricDataDao;
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringProperties monitoringProperties;

    public void cleanup() {
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(monitoringProperties.getMetricExpirationDuration());

        metricDataDao.deleteByTimestampBefore(expiration); //TODO monitor
    }
}
