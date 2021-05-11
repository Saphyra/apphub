package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyChatMessage;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

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

    public static List<String> getSystemMessages(WebDriver driver) {
        return LobbyPage.systemMessages(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
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

    public static void inviteFriend(WebDriver driver, String username) {
        getOnlineFriend(driver, username)
            .click();
    }

    public static WebElement getOnlineFriend(WebDriver driver, String username) {
        return AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getOnlineFriends(driver), webElements -> !webElements.isEmpty())
            .stream()
            .filter(element -> element.getText().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Online friend not found."));
    }

    public static void sendMessage(WebDriver driver, String message) {
        WebElement chatInput = LobbyPage.chatInput(driver);

        clearAndFill(chatInput, message);

        chatInput.sendKeys(Keys.RETURN);
    }

    public static List<LobbyChatMessage> getMessages(WebDriver driver) {
        return LobbyPage.messages(driver)
            .stream()
            .map(LobbyChatMessage::new)
            .collect(Collectors.toList());
    }

    public static void exitLobby(WebDriver driver) {
        LobbyPage.exitButton(driver).click();
    }
}
