package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Surface;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreConstructionActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlanetQueueTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
    public void queueTest() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver, registrationParameters.getUsername());
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

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_LAKE);
        surface.openConstructNewBuildingWindow(driver);

        //Construction item
        SkyXploreConstructionActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        PlanetQueueItem queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Vízszivattyú lvl 0 => 1");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");

        //Terraformation item
        surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_LAKE);
        surface.openTerraformationWindow(driver);
        SkyXploreSurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_DESERT);

        queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Tó => Sivatag");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");
    }
}
