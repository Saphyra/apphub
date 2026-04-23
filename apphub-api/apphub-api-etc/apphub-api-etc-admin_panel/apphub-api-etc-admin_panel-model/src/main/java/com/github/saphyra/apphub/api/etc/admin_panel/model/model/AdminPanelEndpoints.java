package com.github.saphyra.apphub.api.etc.admin_panel.model.model;

public class AdminPanelEndpoints {
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
}
