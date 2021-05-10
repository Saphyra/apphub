package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SkyXploreLobbyActions {
    public static void startGameCreation(WebDriver driver) {
        LobbyPage.startGameCreationButton(driver).click();
    }

    public static void setReady(WebDriver driver) {
        LobbyPage.setReadyButton(driver).click();
    }

    public static LobbyMember getHostMember(WebDriver driver) {
        return new LobbyMember(LobbyPage.hostMember(driver));
    }

    public static boolean pageLoaded(WebDriver driver) {
        return !getSystemMessages(driver).isEmpty();
    }

    private static List<WebElement> getSystemMessages(WebDriver driver) {
        return LobbyPage.systemMessages(driver);
    }

    public static List<WebElement> getOnlineFriends(WebDriver driver) {
        return LobbyPage.onlineFriends(driver);
    }

    public static List<LobbyMember> getMembers(WebDriver driver) {
        return LobbyPage.lobbyMembers(driver)
            .stream()
            .map(LobbyMember::new)
            .collect(Collectors.toList());
    }
}
