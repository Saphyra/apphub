package com.github.saphyra.apphub.integraton.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyChatMessage;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyChatTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String USER_JOINED_TO_LOBBY_TEMPLATE = "%s connected.";
    private static final String MESSAGE_TEXT_1 = "message-1";
    private static final String MESSAGE_TEXT_2 = "message-2";
    private static final String USER_LEFT_LOBBY_TEMPLATE = "%s left the lobby.";

    @Test(groups = "skyxplore")
    public void sendAndReceiveMessages() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreFriendshipActions.setUpFriendship(driver1, driver2, userData1.getUsername(), userData2.getUsername());

        SkyXploreMainMenuActions.createLobby(driver1, GAME_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getSystemMessages(driver1).contains(String.format(USER_JOINED_TO_LOBBY_TEMPLATE, userData1.getUsername())))
            .assertTrue();

        SkyXploreLobbyActions.inviteFriend(driver1, userData2.getUsername());

        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getSystemMessages(driver1).contains(String.format(USER_JOINED_TO_LOBBY_TEMPLATE, userData2.getUsername())))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getSystemMessages(driver2).contains(String.format(USER_JOINED_TO_LOBBY_TEMPLATE, userData2.getUsername())))
            .assertTrue();

        SkyXploreLobbyActions.sendMessage(driver1, MESSAGE_TEXT_1);

        List<LobbyChatMessage> hostMessages = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver1), lobbyChatMessages -> !lobbyChatMessages.isEmpty());
        assertThat(hostMessages).hasSize(1);
        verifyChatMessage(hostMessages.get(0), userData1.getUsername(), true, MESSAGE_TEXT_1);

        List<LobbyChatMessage> memberMessages = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver2), lobbyChatMessages -> !lobbyChatMessages.isEmpty());
        assertThat(memberMessages).hasSize(1);
        verifyChatMessage(memberMessages.get(0), userData1.getUsername(), false, MESSAGE_TEXT_1);

        SkyXploreLobbyActions.sendMessage(driver2, MESSAGE_TEXT_2);

        List<LobbyChatMessage> hostMessages3 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver1), lobbyChatMessages -> lobbyChatMessages.size() == 2);
        verifyChatMessage(hostMessages3.get(0), userData2.getUsername(), false, MESSAGE_TEXT_2);

        List<LobbyChatMessage> memberMessages3 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver2), lobbyChatMessages -> lobbyChatMessages.size() == 2);
        verifyChatMessage(memberMessages3.get(0), userData2.getUsername(), true, MESSAGE_TEXT_2);

        SkyXploreLobbyActions.exitLobby(driver2);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getSystemMessages(driver1).contains(String.format(USER_LEFT_LOBBY_TEMPLATE, userData2.getUsername())))
            .assertTrue();
    }

    private void verifyChatMessage(LobbyChatMessage message, String senderName, boolean ownMessage, String messageText) {
        assertThat(message.getSender()).isEqualTo(senderName);
        assertThat(message.isOwn()).isEqualTo(ownMessage);

        assertThat(message.getText()).isEqualTo(messageText);
    }
}
