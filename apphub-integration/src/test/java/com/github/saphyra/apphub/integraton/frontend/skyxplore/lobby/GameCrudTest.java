package com.github.saphyra.apphub.integraton.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMember;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GameCrudTest extends SeleniumTest {
    @Test(groups = "skyxplore")
    public void gameCrud() {
        List<WebDriver> drivers = extractDrivers(3);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        List<Future<?>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2), new BiWrapper<>(driver3, userData3))
            .map(biWrapper -> SkyXploreUtils.registerAndNavigateToMainMenu(biWrapper.getEntity1(), biWrapper.getEntity2()))
            .collect(Collectors.toList());

        AwaitilityWrapper.create(60, 1)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue();

        SkyXploreFriendshipActions.setUpFriendship(driver1, driver2, userData1.getUsername(), userData2.getUsername());
        SkyXploreFriendshipActions.setUpFriendship(driver1, driver3, userData1.getUsername(), userData3.getUsername());

        SkyXploreMainMenuActions.openCreateGameDialog(driver1);

        //Create lobby - Game name too short
        SkyXploreMainMenuActions.fillGameName(driver1, "aa");
        SkyXploreMainMenuActions.verifyInvalidGameName(driver1, "Játék név túl rövid. (Minimum 3 karakter)");

        //Create lobby - Game name too long
        SkyXploreMainMenuActions.fillGameName(driver1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreMainMenuActions.verifyInvalidGameName(driver1, "Játék név túl hosszú. (Maximum 30 karakter)");

        //Create lobby
        SkyXploreMainMenuActions.fillGameName(driver1, "game-name");
        SkyXploreMainMenuActions.verifyValidGameName(driver1);

        SkyXploreMainMenuActions.submitGameCreationForm(driver1);

        AwaitilityWrapper.createDefault()
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_LOBBY_PAGE))
            .assertTrue("SkyXplore Lobby page is not loaded.");

        //Start game - not ready
        SkyXploreLobbyActions.startGameCreation(driver1);

        ToastMessageUtil.verifyErrorToast(driver1, "Még nincs kész mindenki.");

        //Start game
        SkyXploreLobbyActions.inviteFriend(driver1, userData2.getUsername());
        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());

        SkyXploreLobbyActions.setReady(driver1);
        SkyXploreLobbyActions.setReady(driver2);

        LobbyMember lobbyMember = SkyXploreLobbyActions.findMemberValidated(driver1, userData1.getUsername());
        AwaitilityWrapper.createDefault()
            .until(lobbyMember::isReady)
            .assertTrue();

        SkyXploreLobbyActions.startGameCreation(driver1);
        AwaitilityWrapper.create(60, 1)
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_GAME_PAGE))
            .assertTrue("SkyXplore Game page is not loaded for host.");

        AwaitilityWrapper.create(60, 1)
            .until(() -> driver2.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_GAME_PAGE))
            .assertTrue("SkyXplore Game page is not loaded for member.");

        //Create lobby from existing game
        SkyXploreGameActions.exit(driver1);
        SkyXploreGameActions.exit(driver2);

        SkyXploreMainMenuActions.openSavedGames(driver1);
        AwaitilityWrapper.getListWithWait(() -> SkyXploreMainMenuActions.getSavedGames(driver1), savedGames -> !savedGames.isEmpty())
            .get(0)
            .load(driver1);

        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());

        //Start game - Not all members ready
        SkyXploreLobbyActions.startGameCreation(driver1);

        ToastMessageUtil.verifyErrorToast(driver1, "Még nincs kész mindenki.");

        //Check if not-member friend is not available to invite
        assertThat(SkyXploreLobbyActions.getOnlineFriends(driver1)).isEmpty();

        //Check if member friend is available to invite
        SkyXploreLobbyActions.exitLobby(driver2);
        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXploreLobbyActions.getOnlineFriends(driver1).isEmpty())
            .assertTrue("Exited lobby member not invitable");

        //Start game - Not all members present
        SkyXploreLobbyActions.setReady(driver1);

        SkyXploreLobbyActions.startGameCreation(driver1);
        SleepUtil.sleep(1000);
        SkyXploreLobbyActions.startGameWithMissingPlayers(driver1);

        AwaitilityWrapper.create(120, 5)
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_GAME_PAGE));
    }
}
