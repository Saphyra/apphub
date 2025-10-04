package com.github.saphyra.apphub.lib.config.common.endpoints;

public class AdminPanelEndpoints {
    //Memory monitoring
    public static final String EVENT_TRIGGER_MEMORY_STATUS_UPDATE = "/event/trigger-memory-monitoring";
    public static final String EVENT_MEMORY_MONITORING = "/event/memory-monitoring";
    public static final String WS_CONNECTION_ADMIN_PANEL_MEMORY_MONITORING = "/api/ws/admin-panel/monitoring/memory";
    public static final String ADMIN_PANEL_INTERNAL_REPORT_MEMORY_STATUS = "/internal/api/admin-panel/monitoring/memory";

    //MIGRATION
    public static final String ADMIN_PANEL_MIGRATION_GET_TASKS = "/api/admin-panel/migration";
    public static final String ADMIN_PANEL_MIGRATION_TRIGGER_TASK = "/api/admin-panel/migration/{event}";
    public static final String ADMIN_PANEL_MIGRATION_DELETE_TASK = "/api/admin-panel/migration/{event}";

    //ERROR REPORTING
    public static final String ADMIN_PANEL_INTERNAL_REPORT_ERROR = "/internal/admin-panel/report-error";
    public static final String ADMIN_PANEL_GET_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_GET_ERROR_REPORT = "/api/admin-panel/error-report/{id}";
    public static final String ADMIN_PANEL_DELETE_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_MARK_ERROR_REPORTS = "/api/admin-panel/error-report/mark/{status}";
    public static final String ADMIN_PANEL_DELETE_READ_ERROR_REPORTS = "/api/admin-panel/error-report/read";
    public static final String ADMIN_PANEL_ERROR_REPORT_DELETE_ALL = "/api/admin-panel/error-report/all";

    //Performance reporting
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_GET_TOPIC_STATUS = "/api/admin-panel/performance-reporting/topics";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_ENABLE_TOPIC = "/api/admin-panel/performance-reporting/topics";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_DISABLE_TOPIC = "/api/admin-panel/performance-reporting/topics";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_GET_REPORTS = "/api/admin-panel/performance-reporting/topics/{topic}/reports";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_REPORT = "/allowed-internal/api/admin-panel/performance-reporting/topics/reports";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_EVENT_TOPIC_STATUS_MODIFIED = "/event/admin-panel/performance-reporting/topics";
    public static final String ADMIN_PANEL_PERFORMANCE_REPORTING_DELETE_REPORTS = "/api/admin-panel/performance-reporting/topics/{topic}/reports";
}
