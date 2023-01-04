package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.saphyra.apphub.lib.config.common.Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE;
import static com.github.saphyra.apphub.lib.config.common.Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE;

@Controller
@Slf4j
public class AdminPanelPageController {
    @GetMapping(Endpoints.ADMIN_PANEL_ERROR_REPORT_PAGE)
    public String errorReportPage() {
        log.info("ErrorReport page called.");
        return "admin_panel/error_report";
    }

    @GetMapping(ADMIN_PANEL_ROLE_MANAGEMENT_PAGE)
    public String roleManagementPage() {
        log.info("RoleManagement page called.");
        return "admin_panel/role_management";
    }

    @GetMapping(ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE)
    public String disabledRoleManagementPage() {
        log.info("DisabledRoleManagement page called.");
        return "admin_panel/disabled_role_management";
    }

    @GetMapping(Endpoints.ADMIN_PANEL_BAN_PAGE)
    public String banPage() {
        log.info("Ban page called.");
        return "admin_panel/ban";
    }

    @GetMapping(Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE)
    public String rolesForAllPage() {
        log.info("RolesForAll page called.");
        return "admin_panel/roles_for_all";
    }

    @GetMapping(Endpoints.ADMIN_PANEL_MEMORY_MONITORING_PAGE)
    public String memoryMonitoringPage() {
        log.info("RolesForAll page called.");
        return "admin_panel/memory_monitoring";
    }
}
