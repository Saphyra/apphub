package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
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
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructionTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
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
        surface.openConstructNewBuildingWindow(driver);

        SkyXploreConstructionActions.constructBuilding(driver, Constants.DATA_ID_WATER_PUMP);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).isEqualTo(Constants.DATA_ID_WATER_PUMP);
        assertThat(surface.getBuildingLevel()).isZero();
        assertThat(surface.isConstructionInProgress()).isTrue();

        //Cancel construction
        surface.cancelConstruction(driver);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isTrue();

        //Upgrade building
        surface = SkyXplorePlanetActions.findSurfaceWithUpgradableBuilding(driver);
        surfaceId = surface.getSurfaceId();

        surface.upgradeBuilding();

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isConstructionInProgress()).isTrue();

        //Cancel upgrade

        surface.cancelConstruction(driver);

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isConstructionInProgress()).isFalse();
    }
}
