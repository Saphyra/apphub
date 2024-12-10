package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructConstructionAreaWithBuildingModulesTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void deconstructConstructionAreaWithBuildingModules() {
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

        String surfaceId = SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT)
            .getSurfaceId();

        SkyXploreConstructionAreaActions.constructConstructionArea(driver, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .openConstructionArea();

        SkyXploreConstructionAreaActions.constructBuildingModules(
            driver,
            new BiWrapper<>(Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY, Constants.BUILDING_MODULE_HAMSTER_WHEEL),
            new BiWrapper<>(Constants.BUILDING_MODULE_CATEGORY_SMALL_STORAGE, Constants.BUILDING_MODULE_SMALL_BATTERY)
        );

        SkyXploreConstructionAreaActions.deconstructConstructionArea(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("Queue item amount mismatch");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(driver).size() == 3)
            .assertTrue("QueueItems were not created for BuildingModule deconstruction");

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).size() == 1)
            .assertTrue("BuildingModules were not deconstructed");

        assertThat(SkyXplorePlanetActions.getQueue(driver).get(0).getTitle()).isEqualTo("Deconstructing: Extractor");

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(driver).isEmpty())
            .assertTrue("ConstructionArea is not deconstructed");
    }
}
