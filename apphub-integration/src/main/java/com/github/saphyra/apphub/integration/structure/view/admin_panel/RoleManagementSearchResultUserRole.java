package com.github.saphyra.apphub.integration.structure.view.admin_panel;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class RoleManagementSearchResultUserRole {
    private final WebElement webElement;

    public void grant(WebDriver driver) {
        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("role-management-grant-role-confirmation")))
            .assertTrue("Grant role confirmation dialog is not opened.");
    }

    public String getRole() {
        return WebElementUtils.getClasses(webElement)
            .stream()
            .filter(s -> !s.equals("role-management-role-button"))
            .map(s -> s.replace("role-", ""))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Role cannot be identified."));
    }

    public void revoke(WebDriver driver) {
        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("role-management-revoke-role-confirmation")))
            .assertTrue("Grant role confirmation dialog is not opened.");

    }
}
