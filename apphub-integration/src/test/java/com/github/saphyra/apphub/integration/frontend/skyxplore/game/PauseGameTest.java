package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreModifySurfaceActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PauseGameTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void pauseAndResumeGame() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.isOpened(driver))
            .assertTrue("SolarSystem is not opened.");

        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not opened.");

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_FOREST);
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_DESERT);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).size() == 1)
            .assertTrue("Queue size is not 1");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(100, 1)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).getFirst().getStatus() > 0)
            .assertTrue("QueueItem is not started.");

        SkyXploreGameActions.pauseGame(driver);
        SleepUtil.sleep(1000);
        double status = SkyXplorePlanetActions.getQueue(driver)
            .getFirst()
            .getStatus();

        SleepUtil.sleep(10000);

        assertThat(SkyXplorePlanetActions.getQueue(driver).getFirst().getStatus()).isEqualTo(status);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).getFirst().getStatus() > status)
            .assertTrue("Game is not started.");

        SkyXploreGameActions.pauseGame(driver);
    }
}
