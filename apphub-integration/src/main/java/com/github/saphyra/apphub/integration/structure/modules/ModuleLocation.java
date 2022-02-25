package com.github.saphyra.apphub.integration.structure.modules;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    BAN("admin-panel", "ban", Endpoints.ADMIN_PANEL_BAN_PAGE),
    DISABLED_ROLE_MANAGEMENT("admin-panel", "disabled-role-management", Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE),
    ROLE_MANAGEMENT("admin-panel", "role-management", Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE),
    MEMORY_MONITORING("admin-panel", "memory-monitoring", Endpoints.ADMIN_PANEL_MEMORY_MONITORING_PAGE),
    MANAGE_ACCOUNT("accounts", "account", Endpoints.ACCOUNT_PAGE),
    NOTEBOOK("office", "notebook", Endpoints.NOTEBOOK_PAGE),
    SKYXPLORE("game", "skyxplore", Endpoints.SKYXPLORE_CHARACTER_PAGE, driver -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_CHARACTER_PAGE) || driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE));

    private final String categoryId;
    private final String moduleId;
    private final String pageUrl;
    private final Predicate<WebDriver> predicate;

    public boolean pageLoaded(WebDriver driver) {
        return predicate.test(driver);
    }

    ModuleLocation(String categoryId, String moduleId, String pageUrl) {
        this(categoryId, moduleId, pageUrl, driver -> driver.getCurrentUrl().endsWith(pageUrl));
    }
}
