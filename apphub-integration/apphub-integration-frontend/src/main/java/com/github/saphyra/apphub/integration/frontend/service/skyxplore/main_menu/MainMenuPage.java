package com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class MainMenuPage {
    private static final By BACK_BUTTON = By.id("back-button");
    private static final By EDIT_CHARACTER_BUTTON = By.id("edit-character-button");
    private static final By CREATE_GAME_BUTTON = By.id("new-game-button");
    private static final By CREATE_GAME_DIALOG = By.id("lobby-creation");
    static final By GAME_NAME_INPUT = By.id("lobby-name");
    private static final By INVALID_GAME_NAME = By.id("invalid-lobby-name");
    private static final By SUBMIT_GAME_CREATION_FORM_BUTTON = By.id("create-lobby-button");

    static WebElement backButton(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }

    static WebElement editCharacterButton(WebDriver driver) {
        return driver.findElement(EDIT_CHARACTER_BUTTON);
    }

    static WebElement createGameButton(WebDriver driver) {
        return driver.findElement(CREATE_GAME_BUTTON);
    }

    static WebElement createGameDialog(WebDriver driver) {
        return driver.findElement(CREATE_GAME_DIALOG);
    }

    static WebElement gameNameInput(WebDriver driver) {
        return driver.findElement(GAME_NAME_INPUT);
    }

    static WebElement invalidGameName(WebDriver driver) {
        return driver.findElement(INVALID_GAME_NAME);
    }

    static WebElement submitGameCreationFormButton(WebDriver driver) {
        return driver.findElement(SUBMIT_GAME_CREATION_FORM_BUTTON);
    }
}
