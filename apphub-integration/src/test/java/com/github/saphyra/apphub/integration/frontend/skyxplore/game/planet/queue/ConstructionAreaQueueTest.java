package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet.queue;

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
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructionAreaQueueTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void constructionAreaQueueCrud() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(Constants.DEFAULT_GAME_NAME, driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.isOpened(driver))
            .assertTrue("SolarSystem is not opened.");

        SkyXploreSolarSystemActions.getPlanet(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not opened.");

        SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT)
            .openModifySurfaceWindow(driver);

        //Create queue item
        SkyXploreModifySurfaceActions.constructConstructionArea(driver, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        PlanetQueueItem queueItem = AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXplorePlanetActions.getQueue(driver));
        assertThat(queueItem.getTitle()).isEqualTo("Constructing: Extractor");

        //Check progress
        SkyXploreGameActions.resumeGame(driver);

        PlanetQueueItem q1 = queueItem;
        AwaitilityWrapper.create(120, 5)
            .until(() -> q1.getStatus() > 20)
            .assertTrue("QueueItem is not started.");

        SkyXploreGameActions.pauseGame(driver);

        //Cancel queue item
        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not cancelled.");

        //Finish construction
        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);
        SkyXploreModifySurfaceActions.constructConstructionArea(driver, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not started.");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not finished.");

        SkyXploreGameActions.pauseGame(driver);

        //Create deconstruction queue item
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .deconstructConstructionArea(driver);

        queueItem = AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXplorePlanetActions.getQueue(driver));
        assertThat(queueItem.getTitle()).isEqualTo("Deconstructing: Extractor");

        //Check progress
        SkyXploreGameActions.resumeGame(driver);

        PlanetQueueItem q2 = queueItem;
        AwaitilityWrapper.create(120, 5)
            .until(() -> q2.getStatus() > 20)
            .assertTrue("QueueItem is not started.");

        SkyXploreGameActions.pauseGame(driver);

        //Cancel deconstruction
        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Deconstruction is not cancelled.");

        //Finish deconstruction
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .deconstructConstructionArea(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not started.");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Deconstruction is not finished.");

        SkyXploreGameActions.pauseGame(driver);
    }
}
