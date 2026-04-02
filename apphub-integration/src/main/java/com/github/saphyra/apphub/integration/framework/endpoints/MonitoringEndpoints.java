package com.github.saphyra.apphub.integration.framework.endpoints;

public class MonitoringEndpoints {
    public static final String MONITORING_PAGE = "/web/monitoring";

    public static final String MONITORING_GET_FEATURES = "/api/monitoring/features";
    public static final String MONITORING_GET_FUNCTIONALITIES = "/api/monitoring/features/{feature}/functionalities";
    public static final String MONITORING_GET_SERVICES = "/api/monitoring/{feature}/services";
    public static final String MONITORING_GET_METRICS = "/api/monitoring/metrics/{type}";
}
