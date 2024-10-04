package com.github.saphyra.apphub.integration.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

@Slf4j
public class ExitFromLobbyTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void exitFromLobby() {
        List<WebDriver> drivers = extractDrivers(4);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);
        WebDriver driver4 = drivers.get(3);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        RegistrationParameters userData4 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(
            getServerPort(),
            List.of(
                new BiWrapper<>(driver1, userData1),
                new BiWrapper<>(driver2, userData2),
                new BiWrapper<>(driver3, userData3),
                new BiWrapper<>(driver4, userData4)
            )
        );

        SkyXploreFriendshipActions.setUpFriendship(driver2, driver4, userData2.getUsername(), userData4.getUsername());
        SkyXploreFriendshipActions.setUpFriendship(driver3, driver4, userData3.getUsername(), userData4.getUsername());
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()), new BiWrapper<>(driver3, userData3.getUsername()));

        memberLeft(driver3, driver4, userData4);
        hostLeft(driver1, driver2, driver4, userData4);
    }

    private static void memberLeft(WebDriver driver3, WebDriver driver4, RegistrationParameters userData4) {
        SkyXploreLobbyActions.inviteFriend(driver3, userData4.getUsername());
        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXploreMainMenuActions.getInvitations(driver4).isEmpty())
            .assertTrue("Invitation did not arrive.");

        SkyXploreLobbyActions.exitLobby(driver3);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreMainMenuActions.getInvitations(driver4).isEmpty())
            .assertTrue("Invitation is still present.");
    }

    private static void hostLeft(WebDriver driver1, WebDriver driver2, WebDriver driver4, RegistrationParameters userData4) {
        SkyXploreLobbyActions.inviteFriend(driver2, userData4.getUsername());
        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXploreMainMenuActions.getInvitations(driver4).isEmpty())
            .assertTrue("Invitation did not arrive.");
        SkyXploreLobbyActions.exitLobby(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreMainMenuActions.getInvitations(driver4).isEmpty())
            .assertTrue("Invitation is till present.");

        AwaitilityWrapper.createDefault()
            .until(() -> driver2.getCurrentUrl().endsWith(SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue("Main Menu is not opened.");
    }
}
