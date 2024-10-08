package com.github.saphyra.apphub.integration.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreLobbyEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerStatus;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.LobbyPlayer;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.OnlineFriend;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyInvitationTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void inviteFriendToLobby() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(getServerPort(), List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreFriendshipActions.setUpFriendship(driver1, driver2, userData1.getUsername(), userData2.getUsername());

        SkyXploreMainMenuActions.back(driver2);

        SkyXploreMainMenuActions.createLobby(driver1, GAME_NAME);

        assertThat(SkyXploreLobbyActions.getOnlineFriends(driver1)).isEmpty();

        ModulesPageActions.openModule(getServerPort(), driver2, ModuleLocation.SKYXPLORE);

        OnlineFriend onlineFriend = SkyXploreLobbyActions.getOnlineFriend(driver1, userData2.getUsername());

        onlineFriend.invite();

        LobbyPlayer invitedMember = AwaitilityWrapper.getWithWait(() -> SkyXploreLobbyActions.findPlayer(driver1, userData2.getUsername()), Optional::isPresent)
            .orElseThrow()
            .orElseThrow();
        assertThat(invitedMember.getStatus()).isEqualTo(LobbyPlayerStatus.INVITED);

        onlineFriend.invite();
        ToastMessageUtil.verifyErrorToast(driver1, LocalizedText.SKYXPLORE_LOBBY_INVITATION_SENT_RECENTLY);

        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());

        AwaitilityWrapper.createDefault()
            .until(() -> driver2.getCurrentUrl().endsWith(SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_PAGE))
            .assertTrue("Invited member was not redirected to lobby.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayerValidated(driver1, userData2.getUsername()).getStatus() == LobbyPlayerStatus.NOT_READY)
            .assertTrue("Lobby member status is not 'NOT_READY'");
    }

    @Test(groups = {"fe", "skyxplore"})
    public void invitationCancelledWhenInviterLeftLobby() {
        List<WebDriver> drivers = extractDrivers(3);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(
            getServerPort(),
            List.of(
                new BiWrapper<>(driver1, userData1),
                new BiWrapper<>(driver2, userData2),
                new BiWrapper<>(driver3, userData3)
            )
        );

        SkyXploreFriendshipActions.setUpFriendship(driver1, driver2, userData1.getUsername(), userData2.getUsername());
        SkyXploreFriendshipActions.setUpFriendship(driver2, driver3, userData2.getUsername(), userData3.getUsername());

        SkyXploreMainMenuActions.createLobby(driver1, GAME_NAME);

        SkyXploreLobbyActions.inviteFriend(driver1, userData2.getUsername());
        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());
        SkyXploreLobbyActions.inviteFriend(driver2, userData3.getUsername());

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreMainMenuActions.getInvitations(driver3).size() == 1)
            .assertTrue("Invitation not arrived");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getPlayer(driver1, userData3.getUsername()).getStatus() == LobbyPlayerStatus.INVITED)
            .assertTrue("Invitation did not appear for host");

        SkyXploreLobbyActions.exitLobby(driver2);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayer(driver1, userData3.getUsername()).isEmpty())
            .assertTrue("Invitation did not disappear for host");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreMainMenuActions.getInvitations(driver3).isEmpty())
            .assertTrue("Invitation not disappeared");
    }
}
