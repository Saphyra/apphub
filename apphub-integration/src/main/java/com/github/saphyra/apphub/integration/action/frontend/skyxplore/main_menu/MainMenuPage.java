package com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

class MainMenuPage {
    static final By GAME_NAME_INPUT = By.id("lobby-name");

    static WebElement backButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-home-button"));
    }

    static WebElement editCharacterButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-edit-character-button"));
    }

    static WebElement createGameButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-new-game-button"));
    }

    static Optional<WebElement> createGameDialog(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-new-game"))
            .stream()
            .findFirst();
    }

    static WebElement gameNameInput(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-game-name"));
    }

    static Optional<WebElement> invalidGameName(WebDriver driver) {
        return driver.findElements(By.id("skyxplore-game-name-validation"))
            .stream()
            .findFirst();
    }

    static WebElement submitGameCreationFormButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-create-new-game-button"));
    }

    static WebElement newFriendName(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-contacts-search-input"));
    }

    static List<WebElement> friendCandidates(WebDriver driver) {
        return driver.findElements(By.cssSelector(".skyxplore-friend-candidate"));
    }

    static List<WebElement> incomingFriendRequests(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-main-menu-incoming-friend-requests .skyxplore-contacts-list-item"));
    }

    static List<WebElement> friends(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-main-menu-friends .skyxplore-contacts-list-item"));
    }

    public static List<WebElement> invitations(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-main-menu-invitations .skyxplore-main-menu-invitation"));
    }

    public static List<WebElement> sentFriendRequests(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-main-menu-sent-friend-requests .skyxplore-contacts-list-item"));
    }

    public static WebElement savedGamesWrapper(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-main-menu-saved-games-wrapper"));
    }

    public static WebElement loadGameButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-load-game-button"));
    }

    public static List<WebElement> savedGames(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-main-menu-saved-games-wrapper .skyxplore-saved-game"));
    }

    public static WebElement confirmFriendDeletionButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-remove-friend-button"));
    }
}
