package com.github.saphyra.apphub.api.platform.monitoring.server;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.platform.monitoring.model.MonitoringEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MonitoringController {
    @PutMapping(MonitoringEndpoints.MONITORING_REPORT_METRICS)
    void internalReportMetrics(@PathVariable("service") String service, @RequestBody List<PutMetricsRequest> entries);

    @GetMapping(MonitoringEndpoints.MONITORING_GET_FEATURES)
    List<Feature> getFeatures(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(MonitoringEndpoints.MONITORING_GET_FUNCTIONALITIES)
    List<String> getFunctionalities(@PathVariable("feature") Feature feature, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(MonitoringEndpoints.MONITORING_GET_SERVICES)
    List<String> getServices(
        @PathVariable("feature") Feature feature,
        @RequestParam(name = "functionality", required = false) String functionality,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );

    @GetMapping(MonitoringEndpoints.MONITORING_GET_METRICS)
    List<GetMetricsResponse> getMetrics(
        @PathVariable("type") MetricDataType type,
        @RequestParam("feature") Feature feature,
        @RequestParam(name = "functionality", required = false) String functionality,
        @RequestParam(name = "service", required = false) String service,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );
}
