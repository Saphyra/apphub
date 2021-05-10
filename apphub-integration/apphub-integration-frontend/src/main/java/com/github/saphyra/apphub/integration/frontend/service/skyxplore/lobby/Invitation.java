package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Invitation {
    private final WebElement webElement;

    public String getInvitor() {
        return webElement.findElement(By.cssSelector(":scope .invitation-sender-name")).getText();
    }

    public void accept() {
        webElement.findElement(By.cssSelector(":scope div.invitation-buttons button:first-child")).click();
    }
}
