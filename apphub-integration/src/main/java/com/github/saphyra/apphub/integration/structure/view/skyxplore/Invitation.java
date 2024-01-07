package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Invitation {
    private final WebElement webElement;

    public String getInvitor() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-main-menu-invitation-name span:first-child"))
            .getText();
    }

    public void accept() {
        webElement.findElement(By.cssSelector(":scope .skyxplore-main-menu-invitation-accept-button"))
            .click();
    }
}
