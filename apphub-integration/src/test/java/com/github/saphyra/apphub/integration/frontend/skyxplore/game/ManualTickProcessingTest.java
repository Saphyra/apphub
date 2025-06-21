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
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualTickProcessingTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void manualTickProcessing() {
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

        assertThat(SkyXploreGameActions.getProcessTickButton(driver)).isEmpty();

        DatabaseUtil.addRoleByEmail(registrationParameters.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreGameActions.getProcessTickButton(driver).isPresent())
            .assertTrue("ProcessTick button not found.");

        SkyXploreMapActions.getSolarSystems(driver)
            .get(0)
            .click();
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();

        Surface surface = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT))
            .orElseThrow(() -> new RuntimeException("No empty desert found."));
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructConstructionArea(driver, Constants.CONSTRUCTION_AREA_POWER_PLANT);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).isPresent())
            .assertTrue("Surface is not loaded.");

        for (int i = 0; i < 2; i++) {
            SkyXploreGameActions.processTick(driver);
        }

        assertThat(SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId))
            .returns(false, Surface::isConstructionInProgress)
            .returns(Constants.CONSTRUCTION_AREA_POWER_PLANT, Surface::getConstructionAreaDataId);
    }
}
