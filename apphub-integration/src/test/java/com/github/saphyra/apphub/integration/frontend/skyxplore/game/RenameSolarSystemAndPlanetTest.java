package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
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
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver).click();

        String oldSolarSystemName = AwaitilityWrapper.getWithWait(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver), s -> s.length() > 0)
            .orElseThrow(() -> new RuntimeException("SolarSystem name not loaded."));

        solarSystem_blank(driver, oldSolarSystemName);
        solarSystem_tooLong(driver, oldSolarSystemName);
        solarSystem_rename(driver);
        String oldPlanetName = planet_blank(driver);
        planet_tooLong(driver, oldPlanetName);
        planet_rename(driver);
    }

    private static void solarSystem_blank(WebDriver driver, String oldSolarSystemName) {
        SkyXploreSolarSystemActions.renameSolarSystem(driver, " ");
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(oldSolarSystemName);
    }

    private static void solarSystem_tooLong(WebDriver driver, String oldSolarSystemName) {
        SkyXploreSolarSystemActions.renameSolarSystem(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(oldSolarSystemName);
        NotificationUtil.verifyErrorNotification(driver, "Solar System name too long (Max. 30 characters).");
        NotificationUtil.clearNotifications(driver);
    }

    private static void solarSystem_rename(WebDriver driver) {
        SkyXploreSolarSystemActions.renameSolarSystem(driver, NEW_SOLAR_SYSTEM_NAME);
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(NEW_SOLAR_SYSTEM_NAME);
        SkyXploreSolarSystemActions.closeSolarSystem(driver);
        SkyXploreMapActions.getSolarSystem(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver).equals(NEW_SOLAR_SYSTEM_NAME))
            .assertTrue("SolarSystem name is not changed.");
    }

    private static String planet_blank(WebDriver driver) {
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        String oldPlanetName = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.getPlanetName(driver), s -> s.length() > 0)
            .orElseThrow(() -> new RuntimeException("Planet name not loaded."));

        SkyXplorePlanetActions.renamePlanet(driver, " ");
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetName(driver).equals(oldPlanetName))
            .assertTrue(String.format("Planet name changed. It was '%s'", SkyXplorePlanetActions.getPlanetName(driver)));
        return oldPlanetName;
    }

    private static void planet_tooLong(WebDriver driver, String oldPlanetName) {
        SkyXplorePlanetActions.renamePlanet(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        assertThat(SkyXplorePlanetActions.getPlanetName(driver)).isEqualTo(oldPlanetName);
        NotificationUtil.verifyErrorNotification(driver, "Planet name too long (Max. 30 characters).");
        NotificationUtil.clearNotifications(driver);
    }

    private static void planet_rename(WebDriver driver) {
        SkyXplorePlanetActions.renamePlanet(driver, NEW_PLANET_NAME);
        assertThat(SkyXplorePlanetActions.getPlanetName(driver)).isEqualTo(NEW_PLANET_NAME);
        SkyXplorePlanetActions.closePlanet(driver);
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetName(driver).equals(NEW_PLANET_NAME))
            .assertTrue("Planet name is not changed.");
    }
}
