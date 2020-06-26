package com.github.saphyra.apphub.integration.frontend.service.admin_panel;

import org.openqa.selenium.WebDriver;

public class AdminPanelPageActions {
    public static void openRoleManagementPage(WebDriver driver) {
        AdminPanelPage.roleManagementPageLink(driver).click();
    }

    public static void back(WebDriver driver) {
        AdminPanelPage.back(driver).click();
    }
}
