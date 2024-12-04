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

public class ConstructionAreaCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void constructionAreaCrud() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

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

        String surfaceId = constructConstructionArea(driver);
        cancelConstruction(driver, surfaceId);
        finishConstruction(driver, surfaceId);
        deconstruct(driver, surfaceId);
        cancelDeconstruction(driver, surfaceId);
        finishDeconstruction(driver, surfaceId);
    }

    private static void finishDeconstruction(WebDriver driver, String surfaceId) {
        deconstruct(driver, surfaceId);
        SkyXploreGameActions.resumeGame(driver);
        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isEmpty())
            .assertTrue("ConstructionArea not deconstructed");
        SkyXploreGameActions.pauseGame(driver);
    }

    private static void cancelDeconstruction(WebDriver driver, String surfaceId) {
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .cancelDeconstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isIdleConstructionAreaPresent())
            .assertTrue("Deconstruction not cancelled");
    }

    private static void deconstruct(WebDriver driver, String surfaceId) {
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .deconstructConstructionArea(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isDeconstructionInProgress())
            .assertTrue("Deconstruction not started.");
    }

    private static void finishConstruction(WebDriver driver, String surfaceId) {
        constructConstructionArea(driver, surfaceId);
        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> !SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isConstructionInProgress())
            .assertTrue("Construction is not finished.");
        SkyXploreGameActions.pauseGame(driver);

        assertThat(SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId))
            .returns(true, Surface::isIdleConstructionAreaPresent)
            .returns(Constants.CONSTRUCTION_AREA_EXTRACTOR, Surface::getConstructionAreaDataId);
    }

    private static void cancelConstruction(WebDriver driver, String surfaceId) {
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .cancelConstruction(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isConstructionInProgress())
            .assertTrue("Construction is not cancelled.");
    }

    private static String constructConstructionArea(WebDriver driver) {
        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT);
        String surfaceId = surface.getSurfaceId();

        constructConstructionArea(driver, surfaceId);

        return surfaceId;
    }

    private static void constructConstructionArea(WebDriver driver, String surfaceId) {
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructConstructionArea(driver, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isConstructionInProgress())
            .assertTrue("Construction is not started.");
    }
}
