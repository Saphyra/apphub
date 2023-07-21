package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.disabled_roles.DisabledRolesActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class DisabledRole {
    private final WebElement webElement;

    public String getRole() {
        return webElement.findElement(By.cssSelector(":scope .role-name")).getText();
    }

    public boolean isEnabled() {
        return getCheckbox().isSelected();
    }

    private WebElement getCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .role-enabled input"));
    }

    public void toggle(WebDriver driver) {
        getCheckbox().click();

        AwaitilityWrapper.createDefault()
            .until(() -> DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver))
            .assertTrue("Confirmation dialog is not opened.");
    }
}
