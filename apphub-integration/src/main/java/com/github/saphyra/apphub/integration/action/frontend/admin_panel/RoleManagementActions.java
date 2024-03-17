package com.github.saphyra.apphub.integration.action.frontend.admin_panel;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.admin_panel.RoleManagementSearchResultUser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class RoleManagementActions {
    public static void search(WebDriver driver, String query) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("role-management-search-input")), query);
        driver.findElement(By.id("role-management-search-button"))
            .click();
    }

    public static Optional<RoleManagementSearchResultUser> findUserByEmail(WebDriver driver, String email) {
        return driver.findElements(By.className("role-management-search-result-user"))
            .stream()
            .map(RoleManagementSearchResultUser::new)
            .filter(roleManagementSearchResultUser -> roleManagementSearchResultUser.getEmail().equals(email))
            .findFirst();
    }

    public static void fillPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("role-management-password")), password);
    }

    public static void confirmGrantRole(WebDriver driver) {
        driver.findElement(By.id("role-management-grant-role-button"))
            .click();
    }

    public static void confirmRevokeRole(WebDriver driver) {
        driver.findElement(By.id("role-management-revoke-role-button"))
            .click();
    }
}
