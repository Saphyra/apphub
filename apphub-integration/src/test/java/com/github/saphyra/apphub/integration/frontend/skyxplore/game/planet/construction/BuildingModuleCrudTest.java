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
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.BuildingModule;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildingModuleCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void buildingModuleCrud() {
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

        construct(driver);
        cancelConstruction(driver);
        finishConstruction(driver);
        deconstruct(driver);
        cancelDeconstruction(driver);
        finishDeconstruction(driver);
    }

    private static void finishDeconstruction(WebDriver driver) {
        deconstruct(driver);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY).isEmpty())
            .assertTrue("BuildingModule is not deconstructed.");

        SkyXploreGameActions.pauseGame(driver);
    }

    private static void cancelDeconstruction(WebDriver driver) {
        SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY)
            .getFirst()
            .cancelDeconstruction();

        AwaitilityWrapper.create(10, 1)
            .until(() -> !SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY).getFirst().isConstructionInProgress())
            .assertTrue("Deconstruction is not cancelled.");
    }

    private static void deconstruct(WebDriver driver) {
        SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY)
            .getFirst()
            .deconstruct(driver);

        AwaitilityWrapper.assertWithWaitList(() -> SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY))
            .returns(true, BuildingModule::isDeconstructionInProgress);
    }

    private static void finishConstruction(WebDriver driver) {
        construct(driver);
        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 1)
            .until(() -> !SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY).get(0).isConstructionInProgress())
            .assertTrue("Construction is not finished.");

        SkyXploreGameActions.pauseGame(driver);
    }

    private static void cancelConstruction(WebDriver driver) {
        SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY)
            .getFirst()
            .cancelConstruction();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY).isEmpty())
            .assertTrue("Construction is not cancelled.");
    }

    private static void construct(WebDriver driver) {
        SkyXploreConstructionAreaActions.constructBuildingModule(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY, Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        assertThat(AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXploreConstructionAreaActions.getBuildingModules(driver, Constants.BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY)))
            .returns(Constants.BUILDING_MODULE_HAMSTER_WHEEL, BuildingModule::getDataId)
            .returns(true, BuildingModule::isConstructionInProgress);
    }
}
