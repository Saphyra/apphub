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

    @GetMapping(Endpoints.ADMIN_PANEL_INDEX_PAGE)
    public String adminPanelIndexPage() {
        log.info("Admin panel index page called.");
        return "index";
    }
}
