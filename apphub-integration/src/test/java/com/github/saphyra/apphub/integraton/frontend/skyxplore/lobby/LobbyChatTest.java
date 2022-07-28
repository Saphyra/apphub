package com.github.saphyra.apphub.integraton.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyChatMessage;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyChatTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String USER_JOINED_TO_LOBBY_TEMPLATE = "%s csatlakozott az előszobához.";
    private static final String MESSAGE_TEXT_1 = "message-1";
    private static final String MESSAGE_TEXT_2 = "message-2";
    private static final String MESSAGE_TEXT_3 = "message-3";
    private static final String USER_LEFT_LOBBY_TEMPLATE = "%s kilépett.";

    @Test(groups = "skyxplore")
    public void sendAndReceiveMessages() {
        WebDriver driver1 = extractDriver();
        WebDriver driver2 = extractDriver();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        Future<Void> driverFuture = EXECUTOR_SERVICE.submit(() -> {
            Navigation.toIndexPage(driver2);
            IndexPageActions.registerUser(driver2, userData2);
            ModulesPageActions.openModule(driver2, ModuleLocation.SKYXPLORE);
            SkyXploreCharacterActions.submitForm(driver2);
            return null;
        });

        Navigation.toIndexPage(driver1);
        IndexPageActions.registerUser(driver1, userData1);
        ModulesPageActions.openModule(driver1, ModuleLocation.SKYXPLORE);
        SkyXploreCharacterActions.submitForm(driver1);

        AwaitilityWrapper.create(120, 5)
            .until(driverFuture::isDone)
            .assertTrue("Member player is not created.");

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
        verifyChatMessage(hostMessages.get(0), userData1.getUsername(), true, Arrays.asList(MESSAGE_TEXT_1));

        List<LobbyChatMessage> memberMessages = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver2), lobbyChatMessages -> !lobbyChatMessages.isEmpty());
        assertThat(memberMessages).hasSize(1);
        verifyChatMessage(memberMessages.get(0), userData1.getUsername(), false, Arrays.asList(MESSAGE_TEXT_1));

        SkyXploreLobbyActions.sendMessage(driver1, MESSAGE_TEXT_2);

        List<LobbyChatMessage> hostMessages2 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver1), lobbyChatMessages -> !lobbyChatMessages.isEmpty());
        assertThat(hostMessages2).hasSize(1);
        verifyChatMessage(hostMessages2.get(0), userData1.getUsername(), true, Arrays.asList(MESSAGE_TEXT_1, MESSAGE_TEXT_2));

        List<LobbyChatMessage> memberMessages2 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver2), lobbyChatMessages -> !lobbyChatMessages.isEmpty());
        assertThat(memberMessages2).hasSize(1);
        verifyChatMessage(memberMessages2.get(0), userData1.getUsername(), false, Arrays.asList(MESSAGE_TEXT_1, MESSAGE_TEXT_2));

        SkyXploreLobbyActions.sendMessage(driver2, MESSAGE_TEXT_3);

        List<LobbyChatMessage> hostMessages3 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver1), lobbyChatMessages -> lobbyChatMessages.size() == 2);
        verifyChatMessage(hostMessages3.get(1), userData2.getUsername(), false, Arrays.asList(MESSAGE_TEXT_3));

        List<LobbyChatMessage> memberMessages3 = AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMessages(driver2), lobbyChatMessages -> lobbyChatMessages.size() == 2);
        verifyChatMessage(memberMessages3.get(1), userData2.getUsername(), true, Arrays.asList(MESSAGE_TEXT_3));

        SkyXploreLobbyActions.exitLobby(driver2);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getSystemMessages(driver1).contains(String.format(USER_LEFT_LOBBY_TEMPLATE, userData2.getUsername())))
            .assertTrue();
    }

    private void verifyChatMessage(LobbyChatMessage message, String senderName, boolean ownMessage, List<String> messages) {
        assertThat(message.getSender()).isEqualTo(senderName);
        assertThat(message.isOwn()).isEqualTo(ownMessage);

        assertThat(message.getMessages()).isEqualTo(messages);
    }
}
