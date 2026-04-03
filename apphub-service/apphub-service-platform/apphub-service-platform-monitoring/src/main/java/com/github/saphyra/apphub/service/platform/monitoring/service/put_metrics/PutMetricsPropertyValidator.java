package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class PutMetricsPropertyValidator {
    private final MetricPropertyDao metricPropertyDao;
    private final MetricPropertyFactory metricPropertyFactory;

    void saveOrVerifyProperties(UUID metricId, List<MetricPropertyModel> properties) {
        Map<String, AggregationStrategy> existing = metricPropertyDao.getByMetricId(metricId)
            .stream()
            .collect(Collectors.toMap(MetricProperty::getProperty, MetricProperty::getAggregationStrategy));
        if (existing.isEmpty()) {
            //If no schema present, create it
            properties.stream()
                .map(propertyModel -> metricPropertyFactory.create(metricId, propertyModel.getKey(), propertyModel.getAggregationStrategy()))
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

    private static void throwPropertyMismatchException(UUID metricId, Map<String, AggregationStrategy> existing) {
        throw ExceptionFactory.reportedException(HttpStatus.BAD_REQUEST, "Properties from request do not match saved properties " + existing + " for metricId " + metricId);
    }
}
