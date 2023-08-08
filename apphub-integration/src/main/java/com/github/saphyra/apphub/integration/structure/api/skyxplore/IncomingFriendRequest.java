package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
@Slf4j
public class IncomingFriendRequest {
    private final WebElement webElement;

    public String getSenderName() {
        return webElement.findElement(By.cssSelector(":scope span:first-child"))
            .getText();
    }

    public void accept() {
        webElement.findElement(By.cssSelector(":scope .skyxplore-accept-friend-request-button")).click();
    }

    public void cancel() {
        webElement.findElement(By.cssSelector(":scope .skyxplore-cancel-friend-request-button")).click();
    }
}
