package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RenameSolarSystemAndPlanetTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_SOLAR_SYSTEM_NAME = "new-solar-system-name";
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Test(groups = "skyxplore")
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
        //SolarSystem - Blank
        SkyXploreSolarSystemActions.renameSolarSystem(driver, " ");
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(oldSolarSystemName);

        //SolarSystem - Too long
        SkyXploreSolarSystemActions.renameSolarSystem(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(oldSolarSystemName);
        NotificationUtil.verifyErrorNotification(driver, "Csillagrendszer név túl hosszú (Maximum 30 karakter).");
        NotificationUtil.clearNotifications(driver);

        //SolarSystem - Rename
        SkyXploreSolarSystemActions.renameSolarSystem(driver, NEW_SOLAR_SYSTEM_NAME);
        assertThat(SkyXploreSolarSystemActions.getSolarSystemName(driver)).isEqualTo(NEW_SOLAR_SYSTEM_NAME);
        SkyXploreSolarSystemActions.closeSolarSystem(driver);
        SkyXploreMapActions.getSolarSystem(driver)
            .click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.getSolarSystemName(driver).equals(NEW_SOLAR_SYSTEM_NAME))
            .assertTrue("SolarSystem name is not changed.");

        //Planet - Blank
        SkyXploreSolarSystemActions.getPlanet(driver)
            .click();
        String oldPlanetName = AwaitilityWrapper.getWithWait(() -> SkyXplorePlanetActions.getPlanetName(driver), s -> s.length() > 0)
            .orElseThrow(() -> new RuntimeException("Planet name not loaded."));

        SkyXplorePlanetActions.renamePlanet(driver, " ");
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetName(driver).equals(oldPlanetName))
            .assertTrue("Planet name changed.");

        //Planet - Too long
        SkyXplorePlanetActions.renamePlanet(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        assertThat(SkyXplorePlanetActions.getPlanetName(driver)).isEqualTo(oldPlanetName);
        NotificationUtil.verifyErrorNotification(driver, "Bolygó név túl hosszú (Maximum 30 karakter).");
        NotificationUtil.clearNotifications(driver);

        //Planet - Rename
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
