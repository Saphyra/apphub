package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameChatMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameChatRoom;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


public class SkyXploreGameChatActions {
    public static void openChat(WebDriver driver) {
        if (!GamePage.chat(driver).isDisplayed()) {
            GamePage.chatButton(driver).click();
        }
    }

    public static void postMessageToRoom(WebDriver driver, String roomName, String message) {
        selectChatRoom(driver, roomName);

        WebElement chatInput = GamePage.chatInput(driver);
        clearAndFill(chatInput, message);

        chatInput.sendKeys(Keys.RETURN);
    }

    public static void selectChatRoom(WebDriver driver, String roomName) {
        getRooms(driver)
            .stream()
            .filter(gameChatRoom -> roomName.equals(gameChatRoom.getName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Chat room not found with name " + roomName))
            .select();
    }

    public static List<GameChatRoom> getRooms(WebDriver driver) {
        return GamePage.chatRooms(driver)
            .stream()
            .map(GameChatRoom::new)
            .collect(Collectors.toList());
    }

    public static List<GameChatMessage> getMessages(WebDriver driver) {
        return GamePage.messages(driver)
            .stream()
            .map(GameChatMessage::new)
            .collect(Collectors.toList());
    }

    public static void createChatRoom(WebDriver driver, String roomName, String... invitedUsers) {
        openChat(driver);

        GamePage.openCreateChatRoomDialogButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> GamePage.createChatRoomDialog(driver).isDisplayed())
            .assertTrue("CreateChatRoomDialog is not opened");

        fillNewChatRoomName(driver, roomName);

        Arrays.stream(invitedUsers)
            .forEach(s -> inviteUserToChatRoom(driver, s));

        GamePage.createChatRoomButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> getRooms(driver).stream().map(GameChatRoom::getName).anyMatch(s -> s.equals(roomName)))
            .assertTrue("ChatRoom not created.");
    }

    private static void inviteUserToChatRoom(WebDriver driver, String username) {
        GamePage.playerListForChatRoomCreation(driver)
            .stream()
            .filter(element -> element.findElement(By.cssSelector(":scope span")).getText().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Player not found"))
            .click();
    }

    private static void fillNewChatRoomName(WebDriver driver, String roomName) {
        clearAndFill(GamePage.createChatRoomTitleInput(driver), roomName);
    }
}
