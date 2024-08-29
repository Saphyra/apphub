package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

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
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RenameSolarSystemAndPlanetTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_SOLAR_SYSTEM_NAME = "new-solar-system-name";
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Test(groups = {"fe", "skyxplore"})
    public void renameSolarSystemAndPlanet() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver).click();

        String oldSolarSystemName = AwaitilityWrapper.getWithWait(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver), s -> s.length() > 0)
            .orElseThrow(() -> new RuntimeException("SolarSystem name not loaded."));

        solarSystem_blank(driver);
        solarSystem_tooLong(driver);
        solarSystem_discard(driver, oldSolarSystemName);
        solarSystem_rename(driver);

        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        String oldPlanetName = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.getPlanetName(driver), s -> s.length() > 0)
            .orElseThrow(() -> new RuntimeException("Planet name not loaded."));

        planet_blank(driver);
        planet_tooLong(driver);
        planet_discard(driver, oldPlanetName);
        planet_rename(driver);
    }

    private static void solarSystem_blank(WebDriver driver) {
        SkyXploreSolarSystemActions.enableNameEditing(driver);
        SkyXploreSolarSystemActions.renameSolarSystem(driver, " ");
        SkyXploreSolarSystemActions.saveNewSolarSystemName(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_GAME_SOLAR_SYSTEM_NAME_BLANK);
    }

    private static void solarSystem_tooLong(WebDriver driver) {
        SkyXploreSolarSystemActions.renameSolarSystem(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreSolarSystemActions.saveNewSolarSystemName(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_GAME_SOLAR_SYSTEM_NAME_TOO_LONG);
    }

    private void solarSystem_discard(WebDriver driver, String oldSolarSystemName) {
        SkyXploreSolarSystemActions.renameSolarSystem(driver, NEW_SOLAR_SYSTEM_NAME);
        SkyXploreSolarSystemActions.discardNewSolarSystemName(driver);
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(oldSolarSystemName);
    }

    private static void solarSystem_rename(WebDriver driver) {
        SkyXploreSolarSystemActions.enableNameEditing(driver);
        SkyXploreSolarSystemActions.renameSolarSystem(driver, NEW_SOLAR_SYSTEM_NAME);
        SkyXploreSolarSystemActions.saveNewSolarSystemName(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver).equals(NEW_SOLAR_SYSTEM_NAME))
            .assertTrue("Solar System name is not changed.");

        SkyXploreSolarSystemActions.closeSolarSystem(driver);
        SkyXploreMapActions.getSolarSystem(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver).equals(NEW_SOLAR_SYSTEM_NAME))
            .assertTrue("SolarSystem name is not changed.");
    }

    private static void planet_blank(WebDriver driver) {
        SkyXplorePlanetActions.enableNameEditing(driver);

        SkyXplorePlanetActions.renamePlanet(driver, " ");
        SkyXplorePlanetActions.saveNewPlanetName(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_GAME_PLANET_NAME_BLANK);
    }

    private static void planet_tooLong(WebDriver driver) {
        SkyXplorePlanetActions.renamePlanet(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXplorePlanetActions.saveNewPlanetName(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_GAME_PLANET_NAME_TOO_LONG);
    }

    private void planet_discard(WebDriver driver, String oldPlanetName) {
        SkyXplorePlanetActions.renamePlanet(driver, NEW_PLANET_NAME);
        SkyXplorePlanetActions.discardNewPlanetName(driver);

        assertThat(SkyXplorePlanetActions.getPlanetName(driver)).isEqualTo(oldPlanetName);
    }

    private static void planet_rename(WebDriver driver) {
        SkyXplorePlanetActions.enableNameEditing(driver);
        SkyXplorePlanetActions.renamePlanet(driver, NEW_PLANET_NAME);
        SkyXplorePlanetActions.saveNewPlanetName(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetName(driver).equals(NEW_PLANET_NAME))
            .assertTrue("Planet name is not changed.");

        SkyXplorePlanetActions.closePlanet(driver);
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetName(driver).equals(NEW_PLANET_NAME))
            .assertTrue("Planet name is not changed.");
    }
}
