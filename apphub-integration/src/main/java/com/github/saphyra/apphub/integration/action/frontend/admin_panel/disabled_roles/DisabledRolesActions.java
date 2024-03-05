package com.github.saphyra.apphub.integration.action.frontend.admin_panel.disabled_roles;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.DisabledRole;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class DisabledRolesActions {
    public static DisabledRole findRoleValidated(WebDriver driver, String role){
        return DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(disabledRole -> disabledRole.getRole().equals(role))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
    }

    public static List<DisabledRole> getDisabledRoles(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> driver.findElements(By.className("disabled-role-management-role")),webElements -> !webElements.isEmpty())
            .stream()
            .map(DisabledRole::new)
            .collect(Collectors.toList());
    }

    public static void fillPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("disabled-role-management-password")), password);
    }

    public static void confirmDisableRole(WebDriver driver) {
        driver.findElement(By.id("disabled-role-management-disable-button"))
            .click();
    }

    public static void confirmEnableRole(WebDriver driver) {
        driver.findElement(By.id("disabled-role-management-enable-button"))
            .click();
    }
}
