package com.github.saphyra.apphub.service.admin_panel;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.saphyra.apphub.lib.config.Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE;
import static com.github.saphyra.apphub.lib.config.Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE;

@Controller
@Slf4j
public class PageController {
    @GetMapping(Endpoints.ADMIN_PANEL_ERROR_REPORT_PAGE)
    public String errorReportPage() {
        log.info("ErrorReport page called.");
        return "error_report";
    }

    @GetMapping(ADMIN_PANEL_ROLE_MANAGEMENT_PAGE)
    public String roleManagementPage() {
        log.info("RoleManagement page called.");
        return "role_management";
    }

    @GetMapping(ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE)
    public String disabledRoleManagementPage() {
        log.info("DisabledRoleManagement page called.");
        return "disabled_role_management";
    }

    @GetMapping(Endpoints.ADMIN_PANEL_BAN_PAGE)
    public String banPage() {
        log.info("Ban page called.");
        return "ban";
    }

    @GetMapping(Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE)
    public String rolesForAllPage() {
        log.info("RolesForAll page called.");
        return "roles_for_all";
    }
}
