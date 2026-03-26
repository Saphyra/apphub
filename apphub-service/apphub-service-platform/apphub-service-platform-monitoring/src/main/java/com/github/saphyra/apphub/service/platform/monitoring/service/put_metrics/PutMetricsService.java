package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import com.github.saphyra.apphub.service.platform.monitoring.service.migration.agggregator.MetricPropertyAggregator;
import com.google.common.util.concurrent.Striped;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@Slf4j
//TODO split
//TODO unit test
public class PutMetricsService {
    private static final Striped<Lock> LOCKS = Striped.lock(8);

    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricPropertyDao metricPropertyDao;
    private final MetricDataDao metricDataDao;
    private final IdGenerator idGenerator;
    private final Map<AggregationStrategy, MetricPropertyAggregator> aggregators;

    public PutMetricsService(
        MetricDao metricDao,
        MetricServiceDao metricServiceDao,
        MetricPropertyDao metricPropertyDao,
        MetricDataDao metricDataDao,
        IdGenerator idGenerator,
        List<MetricPropertyAggregator> aggregators
    ) {
        this.metricDao = metricDao;
        this.metricServiceDao = metricServiceDao;
        this.metricPropertyDao = metricPropertyDao;
        this.metricDataDao = metricDataDao;
        this.idGenerator = idGenerator;
        this.aggregators = aggregators.stream()
            .collect(Collectors.toMap(MetricPropertyAggregator::getAggregationStrategy, aggregator -> aggregator));
    }

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
        saveService(service, metric.getMetricId());

        //Verify if each list of properties matches with the metric's schema
        properties.forEach(p -> saveOrVerifyProperties(metric.getMetricId(), p));
        saveMetricData(
            metric.getMetricId(),
            service,
            timestamp,
            //Aggregate the bucket's values to a single record
            aggregate(properties)
        );
    }

    private Map<String, Double> aggregate(List<List<MetricPropertyModel>> properties) {
        return properties.stream()
            .flatMap(Collection::stream)
            //Group properties by key
            .collect(Collectors.groupingBy(MetricPropertyModel::getKey))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> aggregateProperties(e.getValue())));
    }

    private Double aggregateProperties(List<MetricPropertyModel> properties) {
        AggregationStrategy aggregationStrategy = properties.getFirst()
            .getAggregationStrategy();

        MetricPropertyAggregator aggregator = aggregators.get(aggregationStrategy);

        List<Double> values = properties.stream()
            .map(MetricPropertyModel::getValue)
            .toList();

        return aggregator.apply(values);
    }

    private void saveMetricData(UUID metricId, String service, LocalDateTime timestamp, Map<String, Double> properties) {
        MetricData metricData = MetricData.builder()
            .metricDataId(idGenerator.randomUuid())
            .metricId(metricId)
            .service(service)
            .type(MetricDataType.SECOND)
            .timestamp(timestamp)
            .properties(properties)
            .build();
        metricDataDao.save(metricData);
    }

    private void saveOrVerifyProperties(UUID metricId, List<MetricPropertyModel> properties) {
        Map<String, AggregationStrategy> existing = metricPropertyDao.getByMetricId(metricId)
            .stream()
            .collect(Collectors.toMap(MetricProperty::getProperty, MetricProperty::getAggregationStrategy));
        if (existing.isEmpty()) {
            //If no schema present, create it
            properties.stream()
                .map(propertyModel -> MetricProperty.builder()
                    .metricId(metricId)
                    .property(propertyModel.getKey())
                    .aggregationStrategy(propertyModel.getAggregationStrategy())
                    .build())
                .forEach(metricPropertyDao::save);
        } else {
            if (existing.size() != properties.size()) {
                //Number of properties of the request should be the same as the number of stored properties
                throwPropertyMismatchException(metricId, existing);
            }

            Map<String, AggregationStrategy> newProperties = properties.stream()
                .collect(Collectors.toMap(MetricPropertyModel::getKey, MetricPropertyModel::getAggregationStrategy));
            // throw if the existing saved aggregation strategies differ from the ones in the request
            if (!existing.equals(newProperties)) {
                throwPropertyMismatchException(metricId, existing);
            }
        }
    }

    //Register that the given service offers the given metric
    private void saveService(String service, UUID metricId) {
        MetricService metricService = MetricService.builder()
            .metricId(metricId)
            .service(service)
            .build();
        metricServiceDao.save(metricService);
    }

    private static void throwPropertyMismatchException(UUID metricId, Map<String, AggregationStrategy> existing) {
        throw ExceptionFactory.reportedException(HttpStatus.BAD_REQUEST, "Properties from request do not match saved properties " + existing + " for metricId " + metricId);
    }

    private record LockKey(Feature feature, String functionality) {
    }
}
