package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


@AllArgsConstructor
public class CurrentBan {
    private final WebElement webElement;

    public void revoke(WebDriver driver, String password) {
        webElement.findElement(By.cssSelector(":scope td:last-child button"))
            .click();

        clearAndFill(driver.findElement(By.cssSelector("#revoke-role-confirm-password-container input")), password);

        driver.findElement(By.cssSelector("#confirm-revoke-ban .confirmation-dialog-confirm-button"))
            .click();
    }
}
