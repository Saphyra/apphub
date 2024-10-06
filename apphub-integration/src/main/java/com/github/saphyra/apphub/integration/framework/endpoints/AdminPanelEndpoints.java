package com.github.saphyra.apphub.integration.framework.endpoints;

public class AdminPanelEndpoints {
    public static final String ADMIN_PANEL_BAN_PAGE = "/web/admin-panel/ban";
    public static final String ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/disabled-role-management";
    public static final String ADMIN_PANEL_ROLES_FOR_ALL_PAGE = "/web/admin-panel/roles-for-all";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ADMIN_PANEL_MEMORY_MONITORING_PAGE = "/web/admin-panel/memory-monitoring";
    public static final String ADMIN_PANEL_MIGRATION_TASKS_PAGE = "/web/admin-panel/migration-tasks";

    //Memory monitoring
    public static final String WS_CONNECTION_ADMIN_PANEL_MEMORY_MONITORING = "/api/ws/admin-panel/monitoring/memory";

    //MIGRATION
    public static final String ADMIN_PANEL_MIGRATION_GET_TASKS = "/api/admin-panel/migration";
    public static final String ADMIN_PANEL_MIGRATION_TRIGGER_TASK = "/api/admin-panel/migration/{event}";
    public static final String ADMIN_PANEL_MIGRATION_DELETE_TASK = "/api/admin-panel/migration/{event}";
}
