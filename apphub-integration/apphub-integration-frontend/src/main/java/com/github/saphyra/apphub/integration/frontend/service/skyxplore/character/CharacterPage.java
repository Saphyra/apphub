package com.github.saphyra.apphub.integration.frontend.service.skyxplore.character;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class CharacterPage {
    private static final By CHARACTER_NAME_INPUT = By.id("character-name");
    private static final By BOX_TITLE = By.id("character-tab-title");
    private static final By SUBMIT_BUTTON = By.id("save-character");
    private static final By INVALID_CHARACTER_NAME = By.id("invalid-character-name");

    static WebElement characterName(WebDriver driver) {
        return driver.findElement(CHARACTER_NAME_INPUT);
    }

    public static WebElement getBoxTitle(WebDriver driver) {
        return driver.findElement(BOX_TITLE);
    }

    public static WebElement submitButton(WebDriver driver) {
        return driver.findElement(SUBMIT_BUTTON);
    }

    public static WebElement invalidCharacterName(WebDriver driver) {
        return driver.findElement(INVALID_CHARACTER_NAME);
    }
}
