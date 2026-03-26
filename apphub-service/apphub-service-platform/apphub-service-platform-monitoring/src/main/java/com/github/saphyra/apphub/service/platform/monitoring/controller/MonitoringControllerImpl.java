package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.server.MonitoringController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MonitoringControllerImpl implements MonitoringController {
    private final PutMetricsService putMetricsService;
    private final ErrorReporterService errorReporterService;
    private final MetricDao metricDao;
    private final MetricServiceDao metricServiceDao;
    private final MetricDataQueryService metricDataQueryService;
    private final PutMetricsRequestValidator putMetricsRequestValidator;

    @Override
    public void internalReportMetrics(String service, List<PutMetricsRequest> entries) {
        putMetricsRequestValidator.validate(entries);

        entries.stream()
            //Group received metrics by Feature and Functionality
            .collect(Collectors.groupingBy(request -> new BiWrapper<>(request.getFeature(), request.getFunctionality().getName())))
            .forEach((key, requests) -> {
                try {
                    log.debug("Arrived: {}", requests);
                    putMetricsService.putMetrics(service, key.getEntity1(), key.getEntity2(), requests);
                } catch (Exception e) {
                    errorReporterService.report("Failed to put metrics" + requests, e);
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

        UUID metricId = metricDao.findByIdValidated(feature, functionality)
            .getMetricId();

        return metricServiceDao.getByMetricId(metricId)
            .stream()
            .map(MetricService::getService)
            .toList();
    }

    @Override
    public List<GetMetricsResponse> getMetrics(MetricDataType type, Feature feature, String functionality, String service, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the metrics for type {}, feature {}, functionality {} and service {}", accessTokenHeader.getUserId(), type, feature, functionality, service);

        return metricDataQueryService.getMetrics(type, feature, functionality, service);
    }
}
