package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataFactory;
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
    private final PutMetricsAggregator putMetricsAggregator;
    private final PutMetricsPropertyValidator putMetricsPropertyValidator;
    private final MetricServiceFactory metricServiceFactory;
    private final MetricDataFactory metricDataFactory;

    @Transactional
    @SneakyThrows
    public void putMetrics(String service, Feature feature, String functionality, List<PutMetricsRequest> request) {
        //Processing of metrics for the same feature and functionality must be synchronized to avoid concurrent creation of the same metric and metric properties.
        LockKey lockKey = new LockKey(feature, functionality);
        Lock lock = LOCKS.get(lockKey);
        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Could not acquire lock for feature " + feature + " and functionality " + functionality);
        }

        try {
            request.stream()
                //Separate metrics by buckets
                .collect(Collectors.groupingBy(r -> r.getTimestamp().withNano(0)))
                .forEach((timestamp, metrics) ->
                    aggregateAndSave(
                        service,
                        feature,
                        functionality,
                        timestamp,
                        metrics.stream().map(PutMetricsRequest::getProperties).toList()
                    )
                );
        } finally {
            lock.unlock();
        }
    }

    private void aggregateAndSave(String service, Feature feature, String functionality, LocalDateTime timestamp, List<List<MetricPropertyModel>> properties) {
        Metric metric = metricDao.findOrCreate(feature, functionality);
        metricServiceDao.save(metricServiceFactory.create(metric.getMetricId(), service));

        //Verify if each list of properties matches with the metric's schema
        properties.forEach(p -> putMetricsPropertyValidator.saveOrVerifyProperties(metric.getMetricId(), p));
        saveMetricData(
            metric.getMetricId(),
            service,
            timestamp,
            //Aggregate the bucket's values to a single record
            putMetricsAggregator.aggregate(properties)
        );
    }

    private void saveMetricData(UUID metricId, String service, LocalDateTime timestamp, Map<String, Double> properties) {
        MetricData metricData = metricDataFactory.createSecond(metricId, service, timestamp, properties);
        metricDataDao.save(metricData);
    }

    private record LockKey(Feature feature, String functionality) {
    }
}
