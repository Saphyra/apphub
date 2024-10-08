package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.*;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutionResult;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.GameChatMessage;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.GameChatRoom;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.LobbyPlayer;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GameChatTest extends SeleniumTest {
    public static final String GENERAL_ROOM_NAME = "General";
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE_1 = "message-1";
    private static final String ALLIANCE_ROOM_NAME = "Alliance";
    private static final String MESSAGE_2 = "message-2";
    private static final String CUSTOM_ROOM_NAME = "custom-room";
    private static final String MESSAGE_3 = "message-3";

    @Test(groups = {"fe", "skyxplore"})
    public void chatInGame() {
        Integer serverPort = getServerPort();

        List<WebDriver> drivers = extractDrivers(3);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        BiWrapper<WebDriver, RegistrationParameters> player1 = new BiWrapper<>(driver1, userData1);
        BiWrapper<WebDriver, RegistrationParameters> player2 = new BiWrapper<>(driver2, userData2);
        BiWrapper<WebDriver, RegistrationParameters> player3 = new BiWrapper<>(driver3, userData3);

        Stream.of(player1, player2, player3)
            .parallel()
            .map(biWrapper -> EXECUTOR_SERVICE.execute(() -> {
                Navigation.toIndexPage(serverPort, biWrapper.getEntity1());
                IndexPageActions.registerUser(biWrapper.getEntity1(), biWrapper.getEntity2());
                ModulesPageActions.openModule(serverPort, biWrapper.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(biWrapper.getEntity1());
                AwaitilityWrapper.createDefault()
                    .until(() -> biWrapper.getEntity1().getCurrentUrl().endsWith(SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue();
            }))
            .map(FutureWrapper::get)
            .forEach(ExecutionResult::getOrThrow);

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()), new BiWrapper<>(driver3, userData3.getUsername()));

        LobbyPlayer host = SkyXploreLobbyActions.findPlayerValidated(driver1, userData1.getUsername());
        host.changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayerValidated(driver2, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue("Alliance of host did not change.");

        SkyXploreLobbyActions.getPlayer(driver1, userData2.getUsername())
            .changeAllianceTo("1");

        Stream.of(driver1, driver2, driver3)
            .forEach(SkyXploreLobbyActions::setReady);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getPlayers(driver1).stream().allMatch(LobbyPlayer::isReady))
            .assertTrue("Lobby members are not ready.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayerValidated(driver1, userData1.getUsername()).isReady())
            .assertTrue("Host is not ready.");

        SkyXploreLobbyActions.startGameCreation(driver1);

        AwaitilityWrapper.create(10, 1)
            .until(() -> Stream.of(player1, player2, player3).allMatch(player -> SkyXploreGameActions.isGameLoaded(player.getEntity1())))
            .assertTrue("Game not loaded for all users");

        SkyXploreGameChatActions.openChat(driver1);
        SkyXploreGameChatActions.openChat(driver2);
        SkyXploreGameChatActions.openChat(driver3);

        messageTooLong(driver1);
        chatInGeneralRoom(driver1, driver2, driver3, userData1);
        chatInAllianceRoom(driver1, driver2, driver3, userData1);
        chatInCustomRoom(driver1, driver2, driver3, userData1, userData3);
    }

    private void messageTooLong(WebDriver driver) {
        SkyXploreGameChatActions.postMessageToRoom(driver, GENERAL_ROOM_NAME, Stream.generate(() -> "a").limit(1025).collect(Collectors.joining()));

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_CHAT_MESSAGE_TOO_LONG);
    }

    private void chatInGeneralRoom(WebDriver driver1, WebDriver driver2, WebDriver driver3, RegistrationParameters userData1) {
        SkyXploreGameChatActions.postMessageToRoom(driver1, GENERAL_ROOM_NAME, MESSAGE_1);

        verifyMessageArrived(driver1, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, true);
        verifyMessageArrived(driver2, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, false);
        verifyMessageArrived(driver3, GENERAL_ROOM_NAME, userData1.getUsername(), MESSAGE_1, false);
    }

    private void chatInAllianceRoom(WebDriver driver1, WebDriver driver2, WebDriver driver3, RegistrationParameters userData1) {
        SkyXploreGameChatActions.postMessageToRoom(driver1, ALLIANCE_ROOM_NAME, MESSAGE_2);

        verifyMessageArrived(driver1, ALLIANCE_ROOM_NAME, userData1.getUsername(), MESSAGE_2, true);
        verifyMessageArrived(driver2, ALLIANCE_ROOM_NAME, userData1.getUsername(), MESSAGE_2, false);
        verifyMessageNotArrived(driver3, ALLIANCE_ROOM_NAME, MESSAGE_2);
    }

    private void chatInCustomRoom(WebDriver driver1, WebDriver driver2, WebDriver driver3, RegistrationParameters userData1, RegistrationParameters userData3) {
        SkyXploreGameChatActions.createChatRoom(driver1, CUSTOM_ROOM_NAME, userData3.getUsername());
        SkyXploreGameChatActions.postMessageToRoom(driver1, CUSTOM_ROOM_NAME, MESSAGE_3);

        verifyMessageArrived(driver1, CUSTOM_ROOM_NAME, userData1.getUsername(), MESSAGE_3, true);
        verifyMessageArrived(driver3, CUSTOM_ROOM_NAME, userData1.getUsername(), MESSAGE_3, false);
        assertThat(SkyXploreGameChatActions.getRooms(driver2).stream().map(GameChatRoom::getName).collect(Collectors.toList())).doesNotContain(CUSTOM_ROOM_NAME);
    }

    private void verifyMessageArrived(WebDriver driver, String roomName, String from, String message, boolean own) {
        SkyXploreGameChatActions.selectChatRoom(driver, roomName);

        List<GameChatMessage> messages = SkyXploreGameChatActions.getMessages(driver);
        Collections.reverse(messages);

        GameChatMessage chatMessage = messages.stream()
            .filter(gameChatMessage -> gameChatMessage.getFrom().equals(from))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Chat message not found in room " + roomName));

        assertThat(chatMessage.getFrom()).isEqualTo(from);
        assertThat(chatMessage.isOwnMessage()).isEqualTo(own);

        assertThat(chatMessage.getMessage()).isEqualTo(message);
    }

    private void verifyMessageNotArrived(WebDriver driver, String roomName, String message) {
        SkyXploreGameChatActions.selectChatRoom(driver, roomName);

        assertThat(SkyXploreGameChatActions.getMessages(driver).stream().noneMatch(gameChatMessage -> gameChatMessage.getMessage().equals(message))).isTrue();
    }
}
