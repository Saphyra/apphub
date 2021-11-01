package com.github.saphyra.apphub.integration.frontend.model.modules;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    DISABLED_ROLE_MANAGEMENT("admin-panel", "disabled-role-management", Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE),
    ROLE_MANAGEMENT("admin-panel", "role-management", Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE),
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
