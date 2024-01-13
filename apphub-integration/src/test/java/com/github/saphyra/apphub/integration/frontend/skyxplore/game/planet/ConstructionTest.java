package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

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
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetBuildingOverviewItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetBuildingOverviewItemDetails;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructionTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void createAndCancelConstruction() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
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
        String surfaceId = surface.getSurfaceId();
        surface = constructNewBuilding(driver, surface, surfaceId);
        cancelConstruction(driver, surface, surfaceId);
        surface = SkyXplorePlanetActions.findSurfaceWithUpgradableBuilding(driver);
        surfaceId = surface.getSurfaceId();
        surface = upgradeBuilding(driver, surface, surfaceId);
        cancelUpgrade(driver, surface, surfaceId);
    }

    private static Surface constructNewBuilding(WebDriver driver, Surface surface, String surfaceId) {
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        surface = AwaitilityWrapper.getOptionalWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Surface not found."));
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_WATER_PUMP);
        assertThat(surface.getBuildingLevel()).isZero();
        assertThat(surface.isConstructionInProgress()).isTrue();
        return surface;
    }

    private static void cancelConstruction(WebDriver driver, Surface surface, String surfaceId) {
        surface.cancelConstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isEmpty())
            .assertTrue("Construction is not cancelled.");
    }

    private static Surface upgradeBuilding(WebDriver driver, Surface surface, String surfaceId) {
        surface.upgradeBuilding(driver);

        surface = AwaitilityWrapper.getOptionalWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(surface.isConstructionInProgress()).isTrue();
        return surface;
    }

    private static void cancelUpgrade(WebDriver driver, Surface surface, String surfaceId) {
        surface.cancelConstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).filter(surface1 -> !surface1.isConstructionInProgress()).isPresent())
            .assertTrue("Construction is not cancelled.");
    }

    @Test(groups = {"fe", "skyxplore"})
    public void finishConstruction() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
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

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_FOREST);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_CAMP);

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_CAMP);
        assertThat(surface.getBuildingLevel()).isZero();
        assertThat(surface.isConstructionInProgress()).isTrue();

        List<PlanetQueueItem> queueItems = SkyXplorePlanetActions.getQueue(driver);
        assertThat(queueItems).hasSize(1);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_CAMP);
        assertThat(surface.getBuildingLevel()).isEqualTo(1);
        assertThat(surface.isConstructionInProgress()).isFalse();

        PlanetBuildingOverviewItem planetBuildingOverview = SkyXplorePlanetActions.getBuildingOverview(driver)
            .getForSurfaceType(Constants.SURFACE_TYPE_FOREST);

        planetBuildingOverview.toggleDetails();

        PlanetBuildingOverviewItemDetails details = planetBuildingOverview.getForDataId(Constants.DATA_ID_CAMP)
            .orElseThrow(() -> new RuntimeException(Constants.DATA_ID_CAMP + " not found."));
        assertThat(details.getTotalLevel()).isEqualTo(2);
        assertThat(details.getUsedSlots()).isEqualTo(2);
    }

    @Test(groups = {"fe", "skyxplore"})
    public void finishUpgrade() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
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

        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_SOLAR_PANEL);

        String surfaceId = surface.getSurfaceId();
        upgradeBuilding(driver, surface, surfaceId);

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_SOLAR_PANEL);
        assertThat(surface.getBuildingLevel()).isEqualTo(1);
        assertThat(surface.isConstructionInProgress()).isTrue();

        List<PlanetQueueItem> queueItems = SkyXplorePlanetActions.getQueue(driver);
        assertThat(queueItems).hasSize(1);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_SOLAR_PANEL);
        assertThat(surface.getBuildingLevel()).isEqualTo(2);
        assertThat(surface.isConstructionInProgress()).isFalse();

        PlanetBuildingOverviewItem planetBuildingOverview = SkyXplorePlanetActions.getBuildingOverview(driver)
            .getForSurfaceType(Constants.SURFACE_TYPE_DESERT);

        planetBuildingOverview.toggleDetails();

        PlanetBuildingOverviewItemDetails details = planetBuildingOverview.getForDataId(Constants.DATA_ID_SOLAR_PANEL)
            .orElseThrow(() -> new RuntimeException(Constants.DATA_ID_CAMP + " not found."));
        assertThat(details.getTotalLevel()).isEqualTo(2);
        assertThat(details.getUsedSlots()).isEqualTo(1);
    }
}
