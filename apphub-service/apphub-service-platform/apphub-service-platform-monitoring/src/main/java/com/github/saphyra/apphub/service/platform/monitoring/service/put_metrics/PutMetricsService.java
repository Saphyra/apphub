package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataFactory;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceFactory;
import com.google.common.util.concurrent.Striped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PutMetricsService {
    private static final Striped<Lock> LOCKS = Striped.lock(8);

    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricDataDao metricDataDao;
    private final PutMetricsPropertyValidator putMetricsPropertyValidator;
    private final MetricServiceFactory metricServiceFactory;
    private final MetricDataFactory metricDataFactory;

    @Transactional
    @SneakyThrows
    public void putMetrics(String service, PutMetricsRequest metric) {
        //Processing of metrics for the same feature and functionality must be synchronized to avoid concurrent creation of the same metric and metric properties.
        LockKey lockKey = new LockKey(metric.getFeature(), metric.getFunctionality());
        Lock lock = LOCKS.get(lockKey);
        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Could not acquire lock for feature " + metric.getFeature() + " and functionality " + metric.getFunctionality());
        }

        try {
            save(
                service,
                metric.getFeature(),
                metric.getFunctionality(),
                metric.getTimestamp().withNano(0),
                metric.getProperties()
            );
        } finally {
            lock.unlock();
        }
    }

    private void save(String service, Feature feature, String functionality, LocalDateTime timestamp, List<MetricPropertyModel> properties) {
        Metric metric = metricDao.findOrCreate(feature, functionality);
        MetricService metricService = metricServiceFactory.create(metric.getMetricId(), service);
        metricServiceDao.save(metricService);

        //Verify if each list of properties matches with the metric's schema
        putMetricsPropertyValidator.saveOrVerifyProperties(metric.getMetricId(), properties);
        saveMetricData(
            metric.getMetricId(),
            service,
            timestamp,
            properties.stream().collect(Collectors.toMap(MetricPropertyModel::getKey, MetricPropertyModel::getValue))
        );
    }

    private void saveMetricData(UUID metricId, String service, LocalDateTime timestamp, Map<String, Double> properties) {
        MetricData metricData = metricDataFactory.createSecond(metricId, service, timestamp, properties);
        metricDataDao.save(metricData);
    }

    private record LockKey(Feature feature, String functionality) {
    }
}
