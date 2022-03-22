package com.github.saphyra.apphub.integration.structure.modules;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    BAN("admin-panel", "ban", Endpoints.ADMIN_PANEL_BAN_PAGE, "Felhasználók tiltása"),
    DISABLED_ROLE_MANAGEMENT("admin-panel", "disabled-role-management", Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE, "Jobosultságok letiltása / engedélyezése"),
    ROLE_MANAGEMENT("admin-panel", "role-management", Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE, "Jogosultságok kezelése"),
    MEMORY_MONITORING("admin-panel", "memory-monitoring", Endpoints.ADMIN_PANEL_MEMORY_MONITORING_PAGE, "Memória felügyelet"),
    MANAGE_ACCOUNT("accounts", "account", Endpoints.ACCOUNT_PAGE, "Fiók kezelése"),
    NOTEBOOK("office", "notebook", Endpoints.NOTEBOOK_PAGE, "Jegyztefüzet"),
    SKYXPLORE(
        "game",
        "skyxplore",
        Endpoints.SKYXPLORE_CHARACTER_PAGE,
        "SkyXplore",
        driver -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_CHARACTER_PAGE) || driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)
    ),
    HTML("training", "html", Endpoints.TRAINING_HTML_PAGE, "HTML"),
    CSS("training", "css", Endpoints.TRAINING_CSS_PAGE, "CSS"),
    BASICS_OF_PROGRAMMING("training", "basics-of-programming", Endpoints.TRAINING_BASICS_OF_PROGRAMMING_PAGE, "A programozás alapja"),
    JAVASCRIPT("training", "javascript", Endpoints.TRAINING_JAVASCRIPT_PAGE, "JavaScript");

    private final String categoryId;
    private final String moduleId;
    private final String pageUrl;
    private final String label;
    private final Predicate<WebDriver> predicate;

    public boolean pageLoaded(WebDriver driver) {
        return predicate.test(driver);
    }

    ModuleLocation(String categoryId, String moduleId, String pageUrl, String label) {
        this(categoryId, moduleId, pageUrl, label, driver -> driver.getCurrentUrl().endsWith(pageUrl));
    }
}
