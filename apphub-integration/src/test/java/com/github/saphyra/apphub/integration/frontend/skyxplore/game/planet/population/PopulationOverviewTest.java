package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet.population;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePopulationOverviewActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.Citizen;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenOrder;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PopulationOverviewTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String CITIZEN_NAME_PREFIX = "citizen-";

    @Test(groups = {"fe", "skyxplore"})
    public void renameAndOrderCitizens() {
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

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.isOpened(driver))
            .assertTrue("SolarSystem is not opened.");

        SkyXploreSolarSystemActions.getPlanet(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not opened.");

        SkyXplorePlanetActions.openPopulationOverview(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePopulationOverviewActions.isLoaded(driver))
            .assertTrue("PopulationOverview is not opened.");

        List<String> newNames = renameCitizens(driver);
        orderCitizens(driver, newNames);
        displayAndHideSkills(driver);
    }

    private List<String> renameCitizens(WebDriver driver) {
        List<String> citizenNames = SkyXplorePopulationOverviewActions.getCitizens(driver)
            .stream()
            .map(Citizen::getName)
            .toList();

        List<String> newNames = Stream.iterate(0, integer -> integer + 1)
            .limit(citizenNames.size())
            .map(integer -> CITIZEN_NAME_PREFIX + integer)
            .collect(Collectors.toList());

        for (int i = 0; i < citizenNames.size(); i++) {
            String newName = newNames.get(i);
            renameCitizen(driver, citizenNames.get(i), newName);

            AwaitilityWrapper.createDefault()
                .until(() -> SkyXplorePopulationOverviewActions.findCitizen(driver, newName).isPresent())
                .assertTrue("Citizen is not renamed.");
        }
        return newNames;
    }

    private void orderCitizens(WebDriver driver, List<String> newNames) {
        SkyXplorePopulationOverviewActions.orderCitizens(driver, CitizenOrder.DESCENDING);
        verifyCitizenOrder(driver, CitizenOrder.DESCENDING, newNames);

        SkyXplorePopulationOverviewActions.orderCitizens(driver, CitizenOrder.ASCENDING);
        verifyCitizenOrder(driver, CitizenOrder.ASCENDING, newNames);
    }

    private void displayAndHideSkills(WebDriver driver) {
        SkyXplorePopulationOverviewActions.toggleSkillDisplay(driver, "AIMING");
        verifyDisplayedSkillCount(driver);

        SkyXplorePopulationOverviewActions.hideAllSkills(driver);
        verifyDisplayedSkillCount(driver);

        SkyXplorePopulationOverviewActions.showAllSkills(driver);
        verifyDisplayedSkillCount(driver);
    }

    private void verifyDisplayedSkillCount(WebDriver driver) {
        int displayedSkillCount = SkyXplorePopulationOverviewActions.getDisplayedSkillCount(driver);

        SkyXplorePopulationOverviewActions.getCitizens(driver)
            .forEach(citizen -> assertThat(citizen.getDisplayedSkillCount()).isEqualTo(displayedSkillCount));
    }

    private void verifyCitizenOrder(WebDriver driver, CitizenOrder order, List<String> names) {
        List<String> expectedOrder = order.sort(names);
        List<String> citizenNames = SkyXplorePopulationOverviewActions.getCitizens(driver)
            .stream()
            .map(Citizen::getName)
            .collect(Collectors.toList());

        assertThat(expectedOrder).isEqualTo(citizenNames);
    }

    private void renameCitizen(WebDriver driver, String oldName, String newName) {
        Citizen citizen = SkyXplorePopulationOverviewActions.findCitizenValidated(driver, oldName);
        citizen.setName(newName);
    }
}
