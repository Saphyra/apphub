package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class DisabledRole {
    private final WebElement webElement;

    public String getRole() {
        return WebElementUtils.getClasses(webElement)
            .stream()
            .filter(s -> s.startsWith("role-"))
            .map(s -> s.replace("role-", ""))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Role class is not found."));
    }

    public boolean isEnabled() {
        return getCheckbox()
            .isSelected();
    }

    private WebElement getCheckbox() {
        return webElement.findElement(By.className("disabled-role-management-role-enabled"));
    }

    public void disable(WebDriver driver) {
        getCheckbox()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("disabled-role-management-disable-role-confirmation")))
            .assertTrue("Confirmation dialog is not opened.");
    }

    public void enable(WebDriver driver) {
        getCheckbox()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("disabled-role-management-enable-role-confirmation")))
            .assertTrue("Confirmation dialog is not opened.");
    }
}
