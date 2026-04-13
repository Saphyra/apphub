package com.github.saphyra.apphub.lib.event;

public class MonitoringEvent {
    public static final String AGGREGATE_SECOND_METRICS = "monitoring-aggregate-second-metrics";
    public static final String AGGREGATE_MINUTE_METRICS = "monitoring-aggregate-minute-metrics";
    public static final String DELETE_EXPIRED_METRICS = "monitoring-delete-expired-metrics";
    public static final String REPORT_MEMORY_STATUS = "report-memory-status";
    public static final String REPORT_IN_MEMORY_DAO_STATUS = "report-in-memory-dao-status";
    public static final String SEND_COLLECTED_METRICS = "send-collected-metrics";
}
