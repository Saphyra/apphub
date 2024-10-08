package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreBuildingFlow;
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
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlanetQueueTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void queueTest() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(driver, registrationParameters.getUsername());
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

        constructionItem(driver);
        terraformationItem(driver);
        deconstructionItem(driver);
    }

    private static void constructionItem(WebDriver driver) {
        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        PlanetQueueItem queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Water pump level 0 => 1");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");
    }

    private static void terraformationItem(WebDriver driver) {
        PlanetQueueItem queueItem;
        Surface surface;
        surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_LAKE);
        surface.openModifySurfaceWindow(driver);
        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_DESERT);

        queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Terraforming: Lake => Desert");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");
    }

    private static void deconstructionItem(WebDriver driver) {
        SkyXploreBuildingFlow.constructBuilding(driver, Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP);

        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_CAMP);
        surface.deconstructBuilding(driver);

        PlanetQueueItem queueItem = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetActions.getQueue(driver), planetQueueItems -> !planetQueueItems.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet Queue is empty."));

        assertThat(queueItem.getTitle()).isEqualTo("Deconstruct: Camp");

        queueItem.cancelItem(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue is not empty.");
    }
}
