package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@AllArgsConstructor
public class CurrentBan {
    private final WebElement webElement;

    public void revoke() {
        webElement.findElement(By.className("ban-user-banned-role-revoke-button"))
            .click();
    }
}
