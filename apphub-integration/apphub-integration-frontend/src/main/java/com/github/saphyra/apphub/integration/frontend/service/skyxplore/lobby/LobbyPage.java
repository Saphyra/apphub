package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
}
