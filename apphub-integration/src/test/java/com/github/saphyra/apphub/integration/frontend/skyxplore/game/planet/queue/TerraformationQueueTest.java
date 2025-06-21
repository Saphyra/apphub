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
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetQueueItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformationQueueTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void terraformationQueueCrud() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);
        DatabaseUtil.addRoleByEmail(registrationParameters.getEmail(), Constants.ROLE_ADMIN);

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
        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        PlanetQueueItem queueItem = AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXplorePlanetActions.getQueue(driver));
        assertThat(queueItem.getTitle()).isEqualTo("Terraforming: Desert => Concrete");

        //Check progress
        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 1)
            .until(() -> queueItem.getStatus() > 20)
            .assertTrue("QueueItem is not started.");

        SkyXploreGameActions.pauseGame(driver);

        //Cancel queue item
        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Terraformation is not cancelled.");

        //Finish terraformation
        SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT)
            .openModifySurfaceWindow(driver);
        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Terraformation is not cancelled.");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Terraformation is not finished.");

        SkyXploreGameActions.pauseGame(driver);
    }
}
