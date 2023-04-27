package com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

class LobbyPage {
    static WebElement startGameCreationButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-start-game-button"));
    }

    static WebElement setReadyButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-set-ready-button"));
    }

    static List<WebElement> systemMessages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-lobby-chat-content .system-message"));
    }

    static List<WebElement> onlineFriends(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-lobby-friends .skyxplore-lobby-active-friend"));
    }

    static List<WebElement> lobbyMembers(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-lobby-members .skyxplore-lobby-member"));
    }

    static WebElement chatInput(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-chat-input"));
    }

    static List<WebElement> messages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#skyxplore-lobby-chat-content .skyxplore-lobby-message:not(.system-message)"));
    }

    static WebElement exitButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-leave-button"));
    }

    static WebElement startGameAnywaysButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-start-game-anyways"));
    }

    static WebElement maxPlayersPerSolarSystem(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-max-players-per-solar-system"));
    }

    static WebElement additionalSolarSystemsMin(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-additional-solar-systems-min"));
    }

    static WebElement additionalSolarSystemsMax(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-additional-solar-systems-max"));
    }

    static WebElement planetsPerSolarSystemMin(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-planets-per-solar-system-min"));
    }

    static WebElement planetsPerSolarSystemMax(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-planets-per-solar-system-max"));
    }

    static WebElement planetSizeMin(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-planet-size-min"));
    }

    static WebElement planetSizeMax(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-planet-size-max"));
    }

    public static WebElement newAiName(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-create-ai-name"));
    }

    public static WebElement createAiButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-lobby-create-ai-button"));
    }

    public static Optional<WebElement> invalidNewAiName(WebDriver driver) {
        return driver.findElements(By.id("skyxplore-lobby-create-ai-name-validation"))
            .stream()
            .findFirst();
    }

    public static List<WebElement> getAis(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-lobby-ai"));
    }

    public static Optional<WebElement> createAiPanel(WebDriver driver) {
        return driver.findElements(By.id("skyxplore-lobby-create-ai"))
            .stream()
            .findFirst();
    }
}
