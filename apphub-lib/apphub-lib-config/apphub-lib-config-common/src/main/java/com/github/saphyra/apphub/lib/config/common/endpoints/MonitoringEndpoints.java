package com.github.saphyra.apphub.lib.config.common.endpoints;

public class MonitoringEndpoints {
    //Internal
    public static final String MONITORING_REPORT_METRICS = "/allowed-internal/monitoring/metrics/{service}";

    //Event
    public static final String MONITORING_EVENT_AGGREGATE_SECOND_METRICS = "/event/monitoring/aggregation/second";
    public static final String MONITORING_EVENT_AGGREGATE_MINUTE_METRICS = "/event/monitoring/aggregation/minute";
    public static final String MONITORING_EVENT_DELETE_EXPIRED_METRICS = "/event/monitoring/migration/delete-expired";
    public static final String EVENT_REPORT_MEMORY_STATUS = "/event/monitoring/memory-status";
    public static final String EVENT_REPORT_IN_MEMORY_DAO_STATUS = "/event/monitoring/in-memory-dao-status";
    public static final String EVENT_SEND_COLLECTED_METRICS = "/event/monitoring/send-collected-metrics";

    //Public
    public static final String MONITORING_GET_FEATURES = "/api/monitoring/features";
    public static final String MONITORING_GET_FUNCTIONALITIES = "/api/monitoring/features/{feature}/functionalities";
    public static final String MONITORING_GET_SERVICES = "/api/monitoring/{feature}/services";
    public static final String MONITORING_GET_METRICS = "/api/monitoring/metrics/{type}";
}
