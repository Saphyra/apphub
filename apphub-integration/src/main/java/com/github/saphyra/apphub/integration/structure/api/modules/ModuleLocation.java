package com.github.saphyra.apphub.integration.structure.api.modules;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    VILLANY_ATESZ("custom", "villanyatesz", Endpoints.VILLANY_ATESZ_PAGE, "VillanyAtesz"),
    COMMUNITY("community", "community", Endpoints.COMMUNITY_PAGE, "Közösség"),
    CALENDAR("office", "calendar", Endpoints.CALENDAR_PAGE, "Határidőnapló"),
    BAN("admin-panel", "ban", Endpoints.ADMIN_PANEL_BAN_PAGE, "Felhasználók tiltása"),
    DISABLED_ROLE_MANAGEMENT("admin-panel", "disabled-role-management", Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE, "Jobosultságok letiltása / engedélyezése"),
    ROLES_FOR_ALL("admin-panel", "roles-for-all", Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE, "Jogosultság mindenkinek"),
    ROLE_MANAGEMENT("admin-panel", "role-management", Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE, "Jogosultságok kezelése"),
    MEMORY_MONITORING("admin-panel", "memory-monitoring", Endpoints.ADMIN_PANEL_MEMORY_MONITORING_PAGE, "Memória felügyelet"),
    MIGRATION_TASKS("admin-panel", "migration-tasks", Endpoints.ADMIN_PANEL_MIGRATION_TASKS_PAGE, "Migrációs feladatok"),
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
    JAVASCRIPT("training", "javascript", Endpoints.TRAINING_JAVASCRIPT_PAGE, "JavaScript"),
    UTILS_BASE_64("development-utils", "base64", Endpoints.UTILS_BASE64_PAGE, "Base64 Encoder"),
    ;

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
