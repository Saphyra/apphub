package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutionResult;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectionLostTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void playerDisconnected() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        createGame(driver1, driver2, userData1, userData2);

        SkyXploreGameActions.resumeGame(driver1);

        Navigation.toIndexPage(getServerPort(), driver2);

        AwaitilityWrapper.create(15, 1)
            .until(() -> SkyXploreGameActions.isPlayerDisconnectedDialogOpened(driver1))
            .assertTrue("Player disconnected dialog is not opened.");

        assertThat(SkyXploreGameActions.isPaused(driver1)).isTrue();

        Navigation.toUrl(driver2, UrlFactory.create(getServerPort(), SkyXploreGameEndpoints.SKYXPLORE_GAME_PAGE));

        AwaitilityWrapper.create(15, 1)
            .until(() -> SkyXploreGameActions.isPlayerReconnectedDialogOpened(driver1))
            .assertTrue("Player reconnected dialog is not opened.");

        driver1.findElement(By.id("skyxplore-game-player-reconnected-resume-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXploreGameActions.isPaused(driver1))
            .assertTrue("Game is still paused.");
    }

    @Test(groups = {"fe", "skyxplore"})
    public void hostDisconnected() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        createGame(driver1, driver2, userData1, userData2);

        SkyXploreGameActions.resumeGame(driver1);

        Navigation.toIndexPage(getServerPort(), driver1);

        AwaitilityWrapper.create(15, 1)
            .until(() -> SkyXploreGameActions.isPausedNotHost(driver2))
            .assertTrue("Game is not paused.");
    }

    private static void createGame(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
        Integer serverPort = getServerPort();

        BiWrapper<WebDriver, RegistrationParameters> player1 = new BiWrapper<>(driver1, userData1);
        BiWrapper<WebDriver, RegistrationParameters> player2 = new BiWrapper<>(driver2, userData2);
        Stream.of(player1, player2)
            .parallel()
            .map(biWrapper -> EXECUTOR_SERVICE.execute(() -> {
                Navigation.toIndexPage(serverPort, biWrapper.getEntity1());
                IndexPageActions.registerUser(biWrapper.getEntity1(), biWrapper.getEntity2());
                ModulesPageActions.openModule(serverPort, biWrapper.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(biWrapper.getEntity1());
                AwaitilityWrapper.createDefault()
                    .until(() -> biWrapper.getEntity1().getCurrentUrl().endsWith(SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue("Player is not redirected to main menu.");
            }))
            .map(FutureWrapper::get)
            .forEach(ExecutionResult::getOrThrow);

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        Stream.of(driver1, driver2)
            .forEach(SkyXploreLobbyActions::setReady);

        SkyXploreLobbyActions.startGameCreation(driver1);

        AwaitilityWrapper.create(10, 1)
            .until(() -> Stream.of(player1, player2).allMatch(player -> SkyXploreGameActions.isGameLoaded(player.getEntity1())))
            .assertTrue("Game not loaded for all users");
    }
}
