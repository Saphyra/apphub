package com.github.saphyra.apphub.integration.structure.community;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Blacklist {
    private final WebElement webElement;

    public String getUsername() {
        return webElement.findElement(By.cssSelector(":scope > span:first-child"))
            .getText();
    }

    public String getEmail() {
        return webElement.getAttribute("title");
    }

    public void delete() {
        webElement.findElement(By.cssSelector(":scope .list-item-button-wrapper button:first-child"))
            .click();
    }
}
