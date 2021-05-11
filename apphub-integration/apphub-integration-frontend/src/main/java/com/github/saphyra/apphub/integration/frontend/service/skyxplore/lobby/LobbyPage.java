package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class LobbyPage {
    public static WebElement startGameCreationButton(WebDriver driver) {
        return driver.findElement(By.id("start-game-button"));
    }

    public static WebElement setReadyButton(WebDriver driver) {
        return driver.findElement(By.id("set-ready"));
    }

    public static WebElement hostMember(WebDriver driver) {
        return driver.findElement(By.cssSelector("#host .lobby-member"));
    }

    public static List<WebElement> systemMessages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#messages .system-message"));
    }

    public static List<WebElement> onlineFriends(WebDriver driver) {
        return driver.findElements(By.cssSelector("#active-friends-list .friend"));
    }

    public static List<WebElement> lobbyMembers(WebDriver driver) {
        return driver.findElements(By.cssSelector("#members-list .lobby-member"));
    }

    public static WebElement chatInput(WebDriver driver) {
        return driver.findElement(By.id("message-input"));
    }

    public static List<WebElement> messages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#messages .message-sender-container"));
    }

    public static WebElement exitButton(WebDriver driver) {
        return driver.findElement(By.id("back-button"));
    }
}
