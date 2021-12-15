package com.github.saphyra.apphub.integration.frontend.model.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.disabled_roles.DisabledRolesActions;
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
