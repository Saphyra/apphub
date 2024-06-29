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
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformationTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void terraformationCD() {
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

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT);
        String surfaceId = surface.getSurfaceId();
        surface = startTerraformation(driver, surface, surfaceId);
        cancelTerraformation(driver, surface, surfaceId);
    }

    private static Surface startTerraformation(WebDriver driver, Surface surface, String surfaceId) {
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_LAKE);

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);

        assertThat(surface.isTerraformationInProgress()).isTrue();
        return surface;
    }

    private static void cancelTerraformation(WebDriver driver, Surface surface, String surfaceId) {
        surface.cancelTerraformation(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isTerraformationInProgress())
            .assertTrue("Terraformation is not cancelled.");
    }

    @Test(groups = {"fe", "skyxplore"})
    public void finishTerraformation() {
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

        SkyXplorePlanetActions.toggleBuildings(driver);

        int desertSlotCount = SkyXplorePlanetActions.getBuildingOverview(driver)
            .getForSurfaceType(Constants.SURFACE_TYPE_DESERT)
            .getTotalSots();

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isTerraformationInProgress()).isTrue();

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Construction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);

        assertThat(surface.isConstructionInProgress()).isFalse();
        assertThat(surface.getSurfaceType()).isEqualTo(Constants.SURFACE_TYPE_CONCRETE);

        assertThat(SkyXplorePlanetActions.getBuildingOverview(driver).getForSurfaceType(Constants.SURFACE_TYPE_DESERT).getTotalSots()).isEqualTo(desertSlotCount - 1);
        assertThat(SkyXplorePlanetActions.getBuildingOverview(driver).getForSurfaceType(Constants.SURFACE_TYPE_CONCRETE).getTotalSots()).isEqualTo(1);
    }
}
