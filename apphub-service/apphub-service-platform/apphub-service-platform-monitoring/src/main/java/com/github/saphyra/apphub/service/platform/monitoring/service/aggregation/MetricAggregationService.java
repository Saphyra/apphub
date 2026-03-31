package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.agggregator.MetricPropertyAggregator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
//TODO split
//TODO unit test
//TODO monitor aggregation steps
public class MetricAggregationService {
    private final Map<MetricDataType, MetricMigrationDataProvider> migrationDataMap;
    private final MetricDataDao metricDataDao;
    private final ExecutorServiceBean executorServiceBean;
    private final IdGenerator idGenerator;
    private final MetricPropertyDao metricPropertyDao;
    private final Map<AggregationStrategy, MetricPropertyAggregator> aggregators;

    MetricAggregationService(
        List<MetricMigrationDataProvider> metricMigrationDatumProviders,
        MetricDataDao metricDataDao,
        ExecutorServiceBean executorServiceBean,
        IdGenerator idGenerator,
        MetricPropertyDao metricPropertyDao,
        List<MetricPropertyAggregator> aggregators
    ) {
        this.migrationDataMap = metricMigrationDatumProviders.stream()
            .collect(Collectors.toMap(MetricMigrationDataProvider::getType, data -> data));
        this.metricDataDao = metricDataDao;
        this.executorServiceBean = executorServiceBean;
        this.idGenerator = idGenerator;
        this.metricPropertyDao = metricPropertyDao;
        this.aggregators = aggregators.stream()
            .collect(Collectors.toMap(MetricPropertyAggregator::getAggregationStrategy, data -> data));

        List<AggregationStrategy> missingAggregators = Arrays.stream(AggregationStrategy.values())
            .filter(aggregationStrategy -> !this.aggregators.containsKey(aggregationStrategy))
            .toList();

        if (!missingAggregators.isEmpty()) {
            throw new IllegalStateException("Missing aggregators for strategies: " + missingAggregators);
        }
    }

    @Transactional
    public void aggregate(MetricDataType metricDataType) {
        MetricMigrationDataProvider dataProvider = migrationDataMap.get(metricDataType);

        LocalDateTime oldestRecordTimestamp = metricDataDao.findOldest(metricDataType)
            .map(metricData -> {
                log.info("Oldest record: {}", metricData);
                return metricData;
            })
            .map(MetricData::getTimestamp)
            .orElse(LocalDateTime.MAX);
        LocalDateTime expirationEnd = dataProvider.getExpirationTime();
        LocalDateTime expirationStart = dataProvider.step(expirationEnd);
        while (expirationEnd.isAfter(oldestRecordTimestamp)) {
            //Map<<MetricId, Service>, List<MetricDataToAggregate>>
            Map<BiWrapper<UUID, String>, List<MetricData>> metrics = metricDataDao.getByTypeBetween(metricDataType, expirationStart, expirationEnd)
                .stream()
                .collect(Collectors.groupingBy(metricData -> new BiWrapper<>(metricData.getMetricId(), metricData.getService())));

            log.info("Aggregating {} types of records between {} and {}", metrics.size(), expirationStart, expirationEnd);

            LocalDateTime timestamp = expirationEnd;
            executorServiceBean.processCollectionWithWait(metrics.entrySet(), entry -> aggregate(dataProvider, timestamp, entry.getKey().getEntity1(), entry.getKey().getEntity2(), entry.getValue()));

            expirationEnd = expirationStart;
            expirationStart = dataProvider.step(expirationStart);
        }

        log.info("{} migration finished. Oldest record timestamp: {}, expirationStart: {}, expirationEnd: {}", metricDataType, oldestRecordTimestamp, expirationStart, expirationEnd);
    }

    private Void aggregate(MetricMigrationDataProvider dataProvider, LocalDateTime timestamp, UUID metricId, String service, List<MetricData> metrics) {
        log.info("Aggregating {} metrics for metricId {} and service {} to timestamp: {}", metrics.size(), metricId, service, timestamp);
        MetricData aggregated = getMetricData(dataProvider, timestamp, metricId, service, metrics);

        metricDataDao.save(aggregated);
        metricDataDao.deleteAll(metrics);

        return null;
    }

    private MetricData getMetricData(MetricMigrationDataProvider dataProvider, LocalDateTime timestamp, UUID metricId, String service, List<MetricData> metrics) {
        return MetricData.builder()
            .metricDataId(idGenerator.randomUuid())
            .metricId(metricId)
            .service(service)
            .type(dataProvider.getResultType())
            .timestamp(timestamp)
            .properties(aggregateProperties(metricId, metrics))
            .build();
    }

    private Map<String, Double> aggregateProperties(UUID metricId, List<MetricData> metrics) {
        Map<String, AggregationStrategy> aggregationStrategies = metricPropertyDao.getByMetricId(metricId)
            .stream()
            .collect(Collectors.toMap(MetricProperty::getProperty, MetricProperty::getAggregationStrategy));

        return aggregationStrategies.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> aggregateProperty(entry.getValue(), entry.getKey(), metrics)));
    }

    private Double aggregateProperty(AggregationStrategy aggregationStrategy, String property, List<MetricData> metrics) {
        List<Double> values = metrics.stream()
            .filter(metricData -> metricData.getProperties().containsKey(property))
            .map(metricData -> metricData.getProperties().get(property))
            .toList();
        MetricPropertyAggregator aggregator = aggregators.get(aggregationStrategy);

        return aggregator.apply(values);
    }
}
