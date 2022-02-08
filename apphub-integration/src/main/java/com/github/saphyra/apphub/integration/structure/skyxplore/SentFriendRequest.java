package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class SentFriendRequest {
    private final WebElement webElement;

    public String getFriendName() {
        return webElement.findElement(By.cssSelector(":scope div:first-child"))
            .getText();
    }

    public void cancel() {
        webElement.findElement(By.cssSelector(":scope button"))
            .click();
    }
}
