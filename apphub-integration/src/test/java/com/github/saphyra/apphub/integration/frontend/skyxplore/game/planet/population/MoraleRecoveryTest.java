package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet.population;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreModifySurfaceActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePopulationOverviewActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class MoraleRecoveryTest extends SeleniumTest {
    private static final int REFERENCE_MORALE = 9000;

    @Test(groups = {"fe", "skyxplore"})
    public void checkMoraleRecovery() {
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

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.isOpened(driver))
            .assertTrue("SolarSystem is not opened.");

        SkyXploreSolarSystemActions.getPlanet(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not opened.");

        SkyXplorePlanetActions.findEmptySurface(driver, Constants.SURFACE_TYPE_DESERT)
            .openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.startTerraformation(driver, Constants.SURFACE_TYPE_CONCRETE);

        SkyXplorePlanetActions.openPopulationOverview(driver);

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(60, 5)
            .until(() -> SkyXplorePopulationOverviewActions.getCitizens(driver)
                .stream()
                .anyMatch(citizen -> citizen.getStat(Constants.CITIZEN_PROPERTY_MORALE).getValue() < REFERENCE_MORALE)
            )
            .assertTrue("Citizen morale not reduced.");

        AwaitilityWrapper.create(120, 10)
            .until(() -> SkyXplorePopulationOverviewActions.getCitizens(driver)
                .stream()
                .allMatch(citizen -> citizen.getStat(Constants.CITIZEN_PROPERTY_MORALE).getValue() > REFERENCE_MORALE)
            )
            .assertTrue("Citizen morale not restored.");
    }
}
