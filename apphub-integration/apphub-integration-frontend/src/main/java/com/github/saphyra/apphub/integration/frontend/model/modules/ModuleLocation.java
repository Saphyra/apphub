package com.github.saphyra.apphub.integration.frontend.model.modules;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    ADMIN_PANEL("accounts", "admin-panel", Endpoints.ADMIN_PANEL_INDEX_PAGE),
    MANAGE_ACCOUNT("accounts", "account", Endpoints.ACCOUNT_PAGE),
    NOTEBOOK("office", "notebook", Endpoints.NOTEBOOK_PAGE),
    SKYXPLORE("game", "skyxplore", Endpoints.SKYXPLORE_MAIN_MENU_PAGE);

    private final String categoryId;
    private final String moduleId;
    private final String pageUrl;

    public boolean pageLoaded(WebDriver driver) {
        return driver.getCurrentUrl().endsWith(pageUrl);
    }
}
