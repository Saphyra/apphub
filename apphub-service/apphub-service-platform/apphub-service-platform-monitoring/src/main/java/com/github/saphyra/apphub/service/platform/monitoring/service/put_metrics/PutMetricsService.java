package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataType;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class PutMetricsService {
    private final PutMetricsRequestValidator putMetricsRequestValidator;
    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricPropertyDao metricPropertyDao;
    private final MetricDataDao metricDataDao;
    private final IdGenerator idGenerator;

    @Transactional
    public void putMetrics(PutMetricsRequest request) {
        putMetricsRequestValidator.validate(request);

        Metric metric = metricDao.findOrCreate(request.getFeature(), request.getFunctionality().getName());
        saveService(request, metric);
        saveOrVerifyProperties(metric.getMetricId(), request);
        saveMetricData(metric.getMetricId(), request);
    }

    private void saveMetricData(UUID metricId, PutMetricsRequest request) {
        Map<String, Double> properties = request.getProperties()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));

        MetricData metricData = MetricData.builder()
            .metricDataId(idGenerator.randomUuid())
            .metricId(metricId)
            .service(request.getService())
            .type(MetricDataType.SECOND)
            .properties(properties)
            .build();
        metricDataDao.save(metricData);
    }

    private void saveOrVerifyProperties(UUID metricId, PutMetricsRequest request) {
        Map<String, AggregationStrategy> existing = metricPropertyDao.getByMetricId(metricId)
            .stream()
            .collect(Collectors.toMap(MetricProperty::getProperty, MetricProperty::getAggregationStrategy));
        if (existing.isEmpty()) {
            request.getProperties()
                .entrySet()
                .stream()
                .map(entry -> MetricProperty.builder()
                    .metricId(metricId)
                    .property(entry.getKey())
                    .aggregationStrategy(entry.getValue().getAggregationStrategy())
                    .build())
                .forEach(metricPropertyDao::save);
        } else {
            if (existing.size() != request.getProperties().size()) {
                throwPropertyMismatchException(metricId, request, existing);
            }

            Map<String, AggregationStrategy> newProperties = request.getProperties()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAggregationStrategy()));
            // throw if the existing saved aggregation strategies differ from the ones in the request
            if (!existing.equals(newProperties)) {
                throwPropertyMismatchException(metricId, request, existing);
            }
        }
    }

    private static void throwPropertyMismatchException(UUID metricId, PutMetricsRequest request, Map<String, AggregationStrategy> existing) {
        throw ExceptionFactory.reportedException(HttpStatus.BAD_REQUEST, "Properties of " + request + " does not match saved properties " + existing + " for metricId " + metricId);
    }

    private void saveService(PutMetricsRequest request, Metric metric) {
        MetricService metricService = MetricService.builder()
            .metricId(metric.getMetricId())
            .service(request.getService())
            .build();
        metricServiceDao.save(metricService);
    }
}
