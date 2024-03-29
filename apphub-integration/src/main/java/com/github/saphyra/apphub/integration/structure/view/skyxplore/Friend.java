package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Friend {
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope span:first-child"))
            .getText();
    }

    public void remove() {
        webElement.findElement(By.cssSelector(":scope button"))
            .click();
    }
}
