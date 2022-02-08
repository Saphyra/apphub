package com.github.saphyra.apphub.integration.action.frontend.admin_panel.role_management;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class RoleManagementPage {
    private static final By SEARCH_INPUT = By.id("search-input");
    private static final By SEARCH_RESULT = By.cssSelector("#users-table tbody tr");
    private static final By BACK_BUTTON = By.id("back-button");

    public static WebElement searchInput(WebDriver driver) {
        return driver.findElement(SEARCH_INPUT);
    }

    public static List<WebElement> searchResult(WebDriver driver) {
        return driver.findElements(SEARCH_RESULT);
    }

    public static WebElement backButton(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }
}
