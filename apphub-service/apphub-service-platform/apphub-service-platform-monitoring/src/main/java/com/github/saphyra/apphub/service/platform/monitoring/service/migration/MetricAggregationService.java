package com.github.saphyra.apphub.service.platform.monitoring.service.migration;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataType;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
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

        LocalDateTime expirationEnd = dataProvider.getExpirationTime();
        LocalDateTime expirationStart = dataProvider.step(expirationEnd);
        Map<BiWrapper<UUID, String>, List<MetricData>> metrics; //Map<<MetricId, Service>, List<MetricDataToAggregate>>
        do {
            log.info("Aggregating {] records between {} and {}", expirationStart, expirationEnd);

            metrics = metricDataDao.getByTypeBetween(metricDataType, expirationStart, expirationEnd)
                .stream()
                .collect(Collectors.groupingBy(metricData -> new BiWrapper<>(metricData.getMetricId(), metricData.getService())));

            LocalDateTime timestamp = expirationEnd;
            executorServiceBean.processCollectionWithWait(metrics.entrySet(), entry -> aggregate(dataProvider, timestamp, entry.getKey().getEntity1(), entry.getKey().getEntity2(), entry.getValue()));

            expirationEnd = expirationStart;
            expirationStart = dataProvider.step(expirationStart);
        } while (!metrics.isEmpty());
    }

    private Void aggregate(MetricMigrationDataProvider dataProvider, LocalDateTime timestamp, UUID metricId, String service, List<MetricData> metrics) {
        MetricData aggregated = MetricData.builder()
            .metricDataId(idGenerator.randomUuid())
            .metricId(metricId)
            .service(service)
            .type(dataProvider.getResultType())
            .timestamp(timestamp)
            .properties(aggregateProperties(metricId, metrics))
            .build();

        metricDataDao.save(aggregated);
        metricDataDao.deleteAll(metrics);

        return null;
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
