package com.github.saphyra.apphub.integration.action.frontend.skyxplore.character;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

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

    public static Optional<WebElement> invalidCharacterName(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-character-name-validation"))
            .stream()
            .findFirst();
    }
}
