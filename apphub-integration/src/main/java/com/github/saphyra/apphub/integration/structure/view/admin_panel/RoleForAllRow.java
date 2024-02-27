package com.github.saphyra.apphub.integration.structure.view.admin_panel;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class RoleForAllRow {
    private final WebElement webElement;

    public void revokeFromAll(WebDriver driver) {
        webElement.findElement(By.className("roles-for-all-remove-from-all-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("roles-for-all-remove-from-all-confirmation-dialog")))
            .assertTrue("Revoke role from all confirmation dialog is not opened.");
    }

    public void addToAll(WebDriver driver) {
        webElement.findElement(By.className("roles-for-all-add-to-all-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("roles-for-all-add-to-all-confirmation-dialog")))
            .assertTrue("Revoke role from all confirmation dialog is not opened.");
    }
}
