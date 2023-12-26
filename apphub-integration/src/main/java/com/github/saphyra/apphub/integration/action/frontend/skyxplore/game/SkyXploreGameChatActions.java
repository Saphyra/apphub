package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
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
        if (!WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-chat")))) {
            driver.findElement(By.id("skyxplore-game-toggle-chat-button"))
                .click();
        }
    }

    public static void postMessageToRoom(WebDriver driver, String roomName, String message) {
        selectChatRoom(driver, roomName);

        WebElement chatInput = driver.findElement(By.id("skyxplore-game-chat-message-input"));
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
        return driver.findElements(By.className("skyxplore-game-chat-room"))
            .stream()
            .map(GameChatRoom::new)
            .collect(Collectors.toList());
    }

    public static List<GameChatMessage> getMessages(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-chat-message"))
            .stream()
            .map(GameChatMessage::new)
            .collect(Collectors.toList());
    }

    public static void createChatRoom(WebDriver driver, String roomName, String... invitedUsers) {
        openChat(driver);

        driver.findElement(By.id("skyxplore-game-chat-room-create-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-chat-room-creator"))))
            .assertTrue("ChatRoomCreator is not displayed");

        fillNewChatRoomName(driver, roomName);

        Arrays.stream(invitedUsers)
            .forEach(s -> inviteUserToChatRoom(driver, s));

        driver.findElement(By.id("skyxplore-game-chat-room-save-button"))
                .click();

        AwaitilityWrapper.createDefault()
            .until(() -> getRooms(driver).stream().map(GameChatRoom::getName).anyMatch(s -> s.equals(roomName)))
            .assertTrue("ChatRoom not created.");
    }

    private static void inviteUserToChatRoom(WebDriver driver, String username) {
        driver.findElements(By.cssSelector("#skyxplore-game-chat-room-creator-available-players .skyxplore-game-chat-room-creator-player"))
            .stream()
            .filter(element -> element.getText().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Player not found"))
            .click();
    }

    private static void fillNewChatRoomName(WebDriver driver, String roomName) {
        clearAndFill(driver.findElement(By.id("skyxplore-game-chat-room-creator-name-input")), roomName);
    }
}
