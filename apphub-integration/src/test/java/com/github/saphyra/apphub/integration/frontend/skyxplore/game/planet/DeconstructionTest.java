package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreBuildingFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetBuildingOverviewItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructionTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void createAndCancelDeconstruction() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

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

        storageInUse(driver);
        SkyXploreBuildingFlow.constructBuilding(driver, Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP);
        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_CAMP);
        String surfaceId = surface.getSurfaceId();
        surface = deconstruct(driver, surface, surfaceId);
        cancelDeconstruction(driver, surface, surfaceId);
    }

    private static void storageInUse(WebDriver driver) {
        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_HEADQUARTERS);
        surface.deconstructBuilding(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_GAME_STORAGE_USED);

        driver.findElement(By.id("skyxplore-game-planet-surface-confirm-deconstruct-building-cancel-button"))
            .click();
    }

    private static Surface deconstruct(WebDriver driver, Surface surface, String surfaceId) {
        surface.deconstructBuilding(driver);

        surface = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId), Surface::isDeconstructionInProgress)
            .orElseThrow(() -> new RuntimeException("Deconstruction not started"));
        return surface;
    }

    private static void cancelDeconstruction(WebDriver driver, Surface surface, String surfaceId) {
        surface.cancelDeconstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isDeconstructionInProgress())
            .assertTrue("Deconstruction not cancelled");
    }

    @Test(groups = {"fe", "skyxplore"})
    public void finishDeconstruction() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

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

        SkyXploreBuildingFlow.constructBuilding(driver, Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP);

        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_CAMP);
        String surfaceId = surface.getSurfaceId();
        surface.deconstructBuilding(driver);

        AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId), Surface::isDeconstructionInProgress)
            .orElseThrow(() -> new RuntimeException("Deconstruction not started"));

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Deconstruction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isTrue();

        SkyXplorePlanetActions.toggleBuildings(driver);

        PlanetBuildingOverviewItem planetBuildingOverview = SkyXplorePlanetActions.getBuildingOverview(driver)
            .getForSurfaceType(Constants.SURFACE_TYPE_DESERT);

        assertThat(planetBuildingOverview.getUsedSlots()).isZero();
    }
}
