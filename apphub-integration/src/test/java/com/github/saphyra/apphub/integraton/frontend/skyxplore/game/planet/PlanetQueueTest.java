package com.github.saphyra.apphub.integraton.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreModifySurfaceActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Surface;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
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
        surface.openModifySurfaceWindow(driver);

        //Construction item
        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        PlanetQueueItem queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Water pump lvl 0 => 1");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");

        //Terraformation item
        surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_LAKE);
        surface.openModifySurfaceWindow(driver);
        SkyXploreSurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_DESERT);

        queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Lake => Desert");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");

        //Deconstruction item
        surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_SOLAR_PANEL);
        surface.deconstructBuilding(driver);

        queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Deconstruct: Solar panel");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");
    }
}
