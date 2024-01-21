package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

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
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ExitGameTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void exitWithAndWithoutSaving() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver).click();
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();

        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).filter(Surface::isTerraformationInProgress).isPresent())
            .assertTrue("Terraformation is not started.");

        SkyXploreGameActions.exit(driver);

        loadGame(driver);

        surface = AwaitilityWrapper.getOptionalWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Game is not loaded"));

        assertThat(surface.isTerraformationInProgress()).isFalse();

        surface.openModifySurfaceWindow(driver);
        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).filter(Surface::isTerraformationInProgress).isPresent())
            .assertTrue("Terraformation is not started.");

        SkyXploreGameActions.saveAndExit(driver);

        loadGame(driver);

        surface = AwaitilityWrapper.getOptionalWithWait(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Game is not loaded"));

        assertThat(surface.isTerraformationInProgress()).isTrue();
    }

    private static void loadGame(WebDriver driver) {
        SkyXploreMainMenuActions.openSavedGames(driver);
        AwaitilityWrapper.getListWithWait(() -> SkyXploreMainMenuActions.getSavedGames(driver), savedGames -> !savedGames.isEmpty())
            .get(0)
            .load(driver);

        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);
    }
}
