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

import java.util.List;

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

        //Construct new building
        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_LAKE);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_WATER_PUMP);
        assertThat(surface.getBuildingLevel()).isZero();
        assertThat(surface.isConstructionInProgress()).isTrue();

        //Cancel construction
        surface.cancelConstruction(driver);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isTrue();

        //Upgrade building
        surface = SkyXplorePlanetActions.findSurfaceWithUpgradableBuilding(driver);
        surfaceId = surface.getSurfaceId();

        surface.upgradeBuilding(driver);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isConstructionInProgress()).isTrue();

        //Cancel upgrade

        surface.cancelConstruction(driver);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isConstructionInProgress()).isFalse();
    }

    @Test(groups = {"fe", "skyxplore"})
    public void finishConstruction() {
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

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_FOREST);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructBuilding(driver, Constants.DATA_ID_CAMP);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_CAMP);
        assertThat(surface.getBuildingLevel()).isZero();
        assertThat(surface.isConstructionInProgress()).isTrue();

        List<PlanetQueueItem> queueItems = SkyXplorePlanetActions.getQueue(driver);
        assertThat(queueItems).hasSize(1);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(Constants.DATA_ID_CAMP);
        assertThat(surface.getBuildingLevel()).isEqualTo(1);
        assertThat(surface.isConstructionInProgress()).isFalse();
    }
}
