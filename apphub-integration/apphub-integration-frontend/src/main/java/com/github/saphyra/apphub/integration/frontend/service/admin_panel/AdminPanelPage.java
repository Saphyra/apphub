package com.github.saphyra.apphub.integration.frontend.service.admin_panel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class AdminPanelPage {
    private static final By ROLE_MANAGEMENT_PAGE_LINK = By.id("role-management");
    private static final By BACK_BUTTON = By.id("back-button");

    public static WebElement roleManagementPageLink(WebDriver driver) {
        return driver.findElement(ROLE_MANAGEMENT_PAGE_LINK);
    }

    public static WebElement back(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }
}
