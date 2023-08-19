package com.github.saphyra.apphub.integraton.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Surface;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructionTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"fe", "skyxplore"})
    public void createAndCancelDeconstruction() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(Constants.DEFAULT_GAME_NAME, driver, registrationParameters.getUsername());
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
        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_BATTERY);
        String surfaceId = surface.getSurfaceId();
        surface = deconstruct(driver, surface, surfaceId);
        cancelDeconstruction(driver, surface, surfaceId);
    }

    private static void storageInUse(WebDriver driver) {
        Surface surface = SkyXplorePlanetActions.findSurfaceWithBuilding(driver, Constants.DATA_ID_DEPOT);
        surface.deconstructBuilding(driver);

        NotificationUtil.verifyErrorNotification(driver, "Storage still in use. Free up some space before you deconstruct it.");
    }

    private static Surface deconstruct(WebDriver driver, Surface surface, String surfaceId) {
        surface.deconstructBuilding(driver);

        surface = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Surface::isDeconstructionInProgress)
            .orElseThrow(() -> new RuntimeException("Deconstruction not started"));
        return surface;
    }

    private static void cancelDeconstruction(WebDriver driver, Surface surface, String surfaceId) {
        surface.cancelDeconstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).isDeconstructionInProgress())
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
        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(Constants.DEFAULT_GAME_NAME, driver, registrationParameters.getUsername());
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
        surface.deconstructBuilding(driver);

        AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Surface::isDeconstructionInProgress)
            .orElseThrow(() -> new RuntimeException("Deconstruction not started"));

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Deconstruction is not finished.");

        surface = SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId);
        assertThat(surface.isEmpty()).isTrue();
    }
}
