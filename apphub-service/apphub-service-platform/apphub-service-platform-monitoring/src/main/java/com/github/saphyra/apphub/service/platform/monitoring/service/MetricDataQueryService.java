package com.github.saphyra.apphub.service.platform.monitoring.service;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricDataQueryService {
    private static final String INVALID_COMBINATION = "invalid_combination";

    private final MetricDao metricDao;
    private final MetricDataDao metricDataDao;
    private final MonitoringProperties monitoringProperties;
    private final DateTimeUtil dateTimeUtil;

    public List<GetMetricsResponse> getMetrics(MetricDataType type, Feature feature, @Nullable String functionality, @Nullable String service) {
        Map<UUID, Metric> metrics = metricDao.getByFeatureAndOptionalFunctionality(feature, functionality)
            .stream()
            .collect(Collectors.toMap(Metric::getMetricId, metric -> metric));

        if (metrics.isEmpty()) {
            throw ExceptionFactory.notLoggedException(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PARAM,
                CollectionUtils.toMap(
                    new BiWrapper<>(feature.name(), INVALID_COMBINATION),
                    new BiWrapper<>(functionality, INVALID_COMBINATION)
                ),
                "Invalid combination of feature " + feature + " and functionality " + functionality
            );
        }

        Duration expirationDuration = monitoringProperties.getAggregation()
            .get(type)
            .getExpirationDuration();
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .minus(expirationDuration);

        return metricDataDao.getByTypeAndMetricIdInAndServiceAfter(type, metrics.keySet(), service, timestamp)
            .stream()
            .map(metricData -> {
                Metric metric = metrics.get(metricData.getMetricId());
                return GetMetricsResponse.builder()
                    .metricDataId(metricData.getMetricDataId())
                    .feature(metric.getFeature())
                    .functionality(metric.getFunctionality())
                    .service(metricData.getService())
                    .timestamp(dateTimeUtil.toEpochSecond(metricData.getTimestamp()))
                    .properties(metricData.getProperties())
                    .build();
            })
            .toList();
    }
}
