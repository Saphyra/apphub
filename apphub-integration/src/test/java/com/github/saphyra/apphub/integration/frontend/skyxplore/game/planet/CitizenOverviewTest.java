package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetPopulationOverviewActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Citizen;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenOrder;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CitizenOverviewTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String CITIZEN_NAME_PREFIX = "citizen-";

    @Test(groups = {"fe", "skyxplore"}, priority = Integer.MIN_VALUE)
    @Ignore //TODO fix and restore
    public void renameAndOrderCitizens() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver, registrationParameters.getUsername());
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

        SkyXplorePlanetActions.openPopulationOverview(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetPopulationOverviewActions.isLoaded(driver))
            .assertTrue("PopulationOverview is not opened.");

        List<String> newNames = renameCitizens(driver);
        orderCitizens(driver, newNames);
        displayAndHideSkills(driver);
    }

    private List<String> renameCitizens(WebDriver driver) {
        List<String> citizenNames = SkyXplorePlanetPopulationOverviewActions.getCitizens(driver)
            .stream()
            .map(Citizen::getName)
            .toList();

        List<String> newNames = Stream.iterate(0, integer -> integer + 1)
            .limit(citizenNames.size())
            .map(integer -> CITIZEN_NAME_PREFIX + integer)
            .collect(Collectors.toList());

        for (int i = 0; i < citizenNames.size(); i++) {
            renameCitizen(driver, citizenNames.get(i), newNames.get(i));
        }
        return newNames;
    }

    private void orderCitizens(WebDriver driver, List<String> newNames) {
        SkyXplorePlanetPopulationOverviewActions.orderCitizens(driver, CitizenOrder.DESCENDING);
        verifyCitizenOrder(driver, CitizenOrder.DESCENDING, newNames);

        SkyXplorePlanetPopulationOverviewActions.orderCitizens(driver, CitizenOrder.ASCENDING);
        verifyCitizenOrder(driver, CitizenOrder.ASCENDING, newNames);
    }

    private void displayAndHideSkills(WebDriver driver) {
        SkyXplorePlanetPopulationOverviewActions.toggleSkillDisplay(driver, "AIMING");
        verifyDisplayedSkillCount(driver);

        SkyXplorePlanetPopulationOverviewActions.hideAllSkills(driver);
        verifyDisplayedSkillCount(driver);

        SkyXplorePlanetPopulationOverviewActions.showAllSkills(driver);
        verifyDisplayedSkillCount(driver);
    }

    private void verifyDisplayedSkillCount(WebDriver driver) {
        int displayedSkillCount = SkyXplorePlanetPopulationOverviewActions.getDisplayedSkillCount(driver);

        SkyXplorePlanetPopulationOverviewActions.getCitizens(driver)
            .forEach(citizen -> assertThat(citizen.getDisplayedSkillCount()).isEqualTo(displayedSkillCount));
    }

    private void verifyCitizenOrder(WebDriver driver, CitizenOrder order, List<String> names) {
        List<String> expectedOrder = order.sort(names);
        List<String> citizenNames = SkyXplorePlanetPopulationOverviewActions.getCitizens(driver)
            .stream()
            .map(Citizen::getName)
            .collect(Collectors.toList());

        assertThat(expectedOrder).isEqualTo(citizenNames);
    }

    private void renameCitizen(WebDriver driver, String oldName, String newName) {
        Citizen citizen = SkyXplorePlanetPopulationOverviewActions.findCitizen(driver, oldName);
        citizen.setName(driver, newName);
        NotificationUtil.clearNotifications(driver);
    }
}
