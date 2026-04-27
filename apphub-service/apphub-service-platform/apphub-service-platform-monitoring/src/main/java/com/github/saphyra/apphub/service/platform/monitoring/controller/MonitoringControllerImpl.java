package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.server.MonitoringController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import com.github.saphyra.apphub.service.platform.monitoring.service.MetricDataQueryService;
import com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics.PutMetricsRequestValidator;
import com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics.PutMetricsService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MonitoringControllerImpl implements MonitoringController {
    private final PutMetricsService putMetricsService;
    private final ErrorReporterService errorReporterService;
    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricDataQueryService metricDataQueryService;
    private final PutMetricsRequestValidator putMetricsRequestValidator;

    @Override
    public void internalReportMetrics(String service, List<PutMetricsRequest> metrics) {
        putMetricsRequestValidator.validate(metrics);

        metrics.forEach((metric) -> {
            try {
                log.debug("Arrived: {}", metric);
                putMetricsService.putMetrics(service, metric);
            } catch (Exception e) {
                errorReporterService.report("Failed to put metric" + metric, e);
            }
        });
    }

    @Override
    public List<Feature> getFeatures(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the available features", accessTokenHeader.getUserId());

        return metricDao.getFeatures();
    }

    @Override
    public List<String> getFunctionalities(Feature feature, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the available functionalities for feature {}", accessTokenHeader.getUserId(), feature);

        return metricDao.getFunctionalitiesOfFeature(feature);
    }

    @Override
    public List<String> getServices(Feature feature, @Nullable String functionality, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the available services for feature {} and functionality {}", accessTokenHeader.getUserId(), feature, functionality);

        List<UUID> metricIds = metricDao.getByFeatureAndOptionalFunctionality(feature, functionality)
            .stream()
            .map(Metric::getMetricId)
            .toList();

        return metricServiceDao.getByMetricIds(metricIds)
            .stream()
            .map(MetricService::getService)
            .toList();
    }

    @Override
    public List<GetMetricsResponse> getMetrics(MetricDataType type, Feature feature, @Nullable String functionality, @Nullable String service, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the metrics for type {}, feature {}, functionality {} and service {}", accessTokenHeader.getUserId(), type, feature, functionality, service);

        return metricDataQueryService.getMetrics(type, feature, functionality, service);
    }
}
