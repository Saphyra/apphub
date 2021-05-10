package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class IncomingFriendRequest {
    private final WebElement webElement;

    public String getSenderName() {
        return webElement.findElement(By.cssSelector(":scope div:first-child")).getText();
    }

    public void accept() {
        webElement.findElement(By.cssSelector(":scope .friend-list-button button:first-child")).click();
    }
}
