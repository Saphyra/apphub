package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

class GamePage {
    public static List<WebElement> systemMessages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#general-chat-message-container .system-message"));
    }

    public static List<WebElement> solarSystems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#map-svg-container circle"));
    }

    public static WebElement chatButton(WebDriver driver) {
        return driver.findElement(By.id("chat-button"));
    }

    public static WebElement chat(WebDriver driver) {
        return driver.findElement(By.id("chat"));
    }

    public static List<WebElement> chatRooms(WebDriver driver) {
        return driver.findElements(By.cssSelector("#chat-rooms .chat-button"));
    }

    public static List<WebElement> messages(WebDriver driver) {
        return driver.findElements(By.cssSelector(".chat-message-container"))
            .stream()
            .filter(WebElement::isDisplayed)
            .flatMap(element -> element.findElements(By.cssSelector(":scope .message-sender-container")).stream())
            .collect(Collectors.toList());
    }

    public static WebElement chatInput(WebDriver driver) {
        return driver.findElement(By.id("send-message-input"));
    }

    public static WebElement openCreateChatRoomDialogButton(WebDriver driver) {
        return driver.findElement(By.id("open-create-chat-room-dialog"));
    }

    public static WebElement createChatRoomDialog(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-container"));
    }

    public static WebElement createChatRoomTitleInput(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-title-input"));
    }

    public static List<WebElement> playerListForChatRoomCreation(WebDriver driver) {
        return driver.findElements(By.cssSelector("#create-chat-room-players .create-chat-room-player"));
    }

    public static WebElement createChatRoomButton(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-button"));
    }
}
