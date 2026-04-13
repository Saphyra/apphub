package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataFactory;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.data_provider.MetricAggregationDataProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MetricAggregationService {
    private final Map<MetricDataType, MetricAggregationDataProvider> aggregationDataProviders;
    private final MetricDataDao metricDataDao;
    private final ExecutorServiceBean executorServiceBean;
    private final MetricDataFactory metricDataFactory;
    private final MetricPropertyAggregator metricPropertyAggregator;

    MetricAggregationService(
        List<MetricAggregationDataProvider> aggregationDataProviders,
        MetricDataDao metricDataDao,
        ExecutorServiceBean executorServiceBean,
        MetricPropertyAggregator metricPropertyAggregator,
        MetricDataFactory metricDataFactory
    ) {
        this.aggregationDataProviders = aggregationDataProviders.stream()
            .collect(Collectors.toMap(MetricAggregationDataProvider::getType, data -> data));
        this.metricDataDao = metricDataDao;
        this.executorServiceBean = executorServiceBean;
        this.metricDataFactory = metricDataFactory;
        this.metricPropertyAggregator = metricPropertyAggregator;
    }

    @Transactional
    public void aggregate(MetricDataType metricDataType) {
        MetricAggregationDataProvider dataProvider = aggregationDataProviders.get(metricDataType);

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

            log.info("Aggregating {} metric of type {} between {} and {}", metrics.size(), metricDataType, expirationStart, expirationEnd);

            LocalDateTime timestamp = expirationEnd;
            executorServiceBean.processCollectionWithWait(
                metrics.entrySet(),
                entry -> aggregate(dataProvider, timestamp, entry.getKey().getEntity1(), entry.getKey().getEntity2(), entry.getValue())
            );

            expirationEnd = expirationStart;
            expirationStart = dataProvider.step(expirationStart);
        }

        log.info("{} aggregation finished. Oldest record timestamp: {}, expirationStart: {}, expirationEnd: {}", metricDataType, oldestRecordTimestamp, expirationStart, expirationEnd);
    }

    private Void aggregate(MetricAggregationDataProvider dataProvider, LocalDateTime timestamp, UUID metricId, String service, List<MetricData> metrics) {
        log.info("Aggregating {} metric of type {} for metricId {} and service {} to timestamp: {}", metrics.size(), dataProvider.getType(), metricId, service, timestamp);
        Map<String, Double> aggregatedProperties = metricPropertyAggregator.aggregateProperties(metricId, metrics);
        MetricData aggregated = metricDataFactory.create(metricId, service, timestamp, dataProvider.getResultType(), aggregatedProperties);

        metricDataDao.save(aggregated);
        metricDataDao.deleteAll(metrics);

        return null;
    }
}
