package com.github.saphyra.apphub.integration.frontend.service.admin_panel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class AdminPanelPage {
    private static final By ROLE_MANAGEMENT_PAGE_LINK = By.id("role-management");
    private static final By BACK_BUTTON = By.id("back-button");

    public static WebElement roleManagementPageLink(WebDriver driver) {
        return driver.findElement(ROLE_MANAGEMENT_PAGE_LINK);
    }

    public static WebElement back(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }

    public static WebElement disabledRoleManagementPageLink(WebDriver driver) {
        return driver.findElement(By.id("disabled-role-management"));
    }

    public static List<WebElement> disabledRoles(WebDriver driver) {
        return driver.findElements(By.cssSelector("#roles tr"));
    }

    public static WebElement toggleDisabledRoleConfirmationDialog(WebDriver driver) {
        return driver.findElement(By.id("confirm-operation"));
    }

    public static WebElement toggleDisabledRoleConfirmationDialogConfirmButton(WebDriver driver) {
        return driver.findElement(By.cssSelector("#confirm-operation .confirmation-dialog-confirm-button"));
    }

    public static WebElement toggleDisabledRoleConfirmationDialogPassword(WebDriver driver) {
        return driver.findElement(By.cssSelector("#confirm-operation [type=password]"));
    }
}
