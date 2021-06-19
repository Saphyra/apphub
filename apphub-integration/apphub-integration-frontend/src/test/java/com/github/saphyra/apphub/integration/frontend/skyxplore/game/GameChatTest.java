package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameChatMessage;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameChatRoom;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GameChatTest extends SeleniumTest {
    public static final String GENERAL_ROOM_NAME = "Általános";
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE_1 = "message-1";
    private static final String ALLIANCE_ROOM_NAME = "Szövetség";
    private static final String MESSAGE_2 = "message-2";
    private static final String CUSTOM_ROOM_NAME = "custom-room";
    private static final String MESSAGE_3 = "message-3";

    @Test(groups = "skyxplore")
    public void chatInGame() {
        WebDriver driver1 = extractDriver();
        WebDriver driver2 = extractDriver();
        WebDriver driver3 = extractDriver();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        BiWrapper<WebDriver, RegistrationParameters> player1 = new BiWrapper<>(driver1, userData1);
        BiWrapper<WebDriver, RegistrationParameters> player2 = new BiWrapper<>(driver2, userData2);
        BiWrapper<WebDriver, RegistrationParameters> player3 = new BiWrapper<>(driver3, userData3);
        List<Future<Object>> futures = Stream.of(player1, player2, player3)
            .map(biWrapper -> EXECUTOR_SERVICE.submit(() -> {
                Navigation.toIndexPage(biWrapper.getEntity1());
                IndexPageActions.registerUser(biWrapper.getEntity1(), biWrapper.getEntity2());
                ModulesPageActions.openModule(biWrapper.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(biWrapper.getEntity1());
                new WebDriverWait(biWrapper.getEntity1(), Duration.ofSeconds(10))
                    .until(ExpectedConditions.textToBe(By.id("main-title"), "Főmenü"));
                return null;
            }))
            .collect(Collectors.toList());

        AwaitilityWrapper.create(120, 5)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue("Failed to set up character(s).");

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()), new BiWrapper<>(driver3, userData3.getUsername()));

        LobbyMember host = SkyXploreLobbyActions.getHostMember(driver1);
        host.changeAllianceTo(Constants.NEW_ALLIANCE_VALUE);

        SkyXploreLobbyActions.getMember(driver1, userData2.getUsername())
            .changeAllianceTo("1");

        Stream.of(driver1, driver2, driver3)
            .forEach(SkyXploreLobbyActions::setReady);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMembers(driver1).stream().allMatch(LobbyMember::isReady))
            .assertTrue("Lobby members are not ready.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getHostMember(driver1).isReady())
            .assertTrue("Host is not ready.");

        SkyXploreLobbyActions.startGameCreation(driver1);

        AwaitilityWrapper.create(60, 2)
            .until(() -> Stream.of(player1, player2, player3).allMatch(player -> SkyXploreGameActions.isGameLoaded(player.getEntity1())))
            .assertTrue("Game not loaded for all users");

        SkyXploreGameChatActions.openChat(driver1);
        SkyXploreGameChatActions.openChat(driver2);
        SkyXploreGameChatActions.openChat(driver3);

        //Chat in general room
        SkyXploreGameChatActions.postMessageToRoom(driver1, GENERAL_ROOM_NAME, MESSAGE_1);

        verifyMessageArrived(driver1, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, true);
        verifyMessageArrived(driver2, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, false);
        verifyMessageArrived(driver3, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, false);

        //Chat in alliance room
        SkyXploreGameChatActions.postMessageToRoom(driver1, ALLIANCE_ROOM_NAME, MESSAGE_2);

        verifyMessageArrived(driver1, ALLIANCE_ROOM_NAME, userData1.getUsername(), MESSAGE_2, true);
        verifyMessageArrived(driver2, ALLIANCE_ROOM_NAME, userData1.getUsername(), MESSAGE_2, false);
        verifyMessageNotArrived(driver3, ALLIANCE_ROOM_NAME);

        //Chat in custom room
        SkyXploreGameChatActions.createChatRoom(driver1, CUSTOM_ROOM_NAME, userData3.getUsername());
        SkyXploreGameChatActions.postMessageToRoom(driver1, CUSTOM_ROOM_NAME, MESSAGE_3);

        verifyMessageArrived(driver1, CUSTOM_ROOM_NAME, userData1.getUsername(), MESSAGE_3, true);
        verifyMessageArrived(driver3, CUSTOM_ROOM_NAME, userData1.getUsername(), MESSAGE_3, false);
        assertThat(SkyXploreGameChatActions.getRooms(driver2).stream().map(GameChatRoom::getName).collect(Collectors.toList())).doesNotContain(CUSTOM_ROOM_NAME);
    }

    private void verifyMessageArrived(WebDriver driver, String roomName, String from, String message, boolean own) {
        SkyXploreGameChatActions.selectChatRoom(driver, roomName);

        GameChatMessage chatMessage = SkyXploreGameChatActions.getMessages(driver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Chat message not found in room " + roomName));

        assertThat(chatMessage.getFrom()).isEqualTo(from);
        assertThat(chatMessage.isOwnMessage()).isEqualTo(own);

        assertThat(chatMessage.getMessages()).containsExactly(message);
    }

    private void verifyMessageNotArrived(WebDriver driver, String roomName) {
        SkyXploreGameChatActions.selectChatRoom(driver, roomName);

        assertThat(SkyXploreGameChatActions.getMessages(driver)).isEmpty();
    }
}
