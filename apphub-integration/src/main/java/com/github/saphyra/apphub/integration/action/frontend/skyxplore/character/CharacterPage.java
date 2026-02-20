package com.github.saphyra.apphub.integration.action.frontend.skyxplore.character;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class CharacterPage {
    static WebElement characterName(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-character-name"));
    }

    public static WebElement getBoxTitle(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-character-details-title"));
    }

    public static WebElement submitButton(WebDriver driver) {
        return driver.findElement(By.id("save-character-button"));
    }
}
