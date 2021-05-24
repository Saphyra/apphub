package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Citizen;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.CitizenOrder;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetPopulationOverviewActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CitizenOverviewTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String CITIZEN_NAME_PREFIX = "citizen-";

    @Test
    public void renameAndOrderCitizens() {
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
            .assertTrue("StorageSettings is not opened.");

        //Rename citizens
        List<String> citizenNames = SkyXplorePlanetPopulationOverviewActions.getCitizens(driver)
            .stream()
            .map(Citizen::getName)
            .collect(Collectors.toList());

        List<String> newNames = Stream.iterate(0, integer -> integer + 1)
            .limit(citizenNames.size())
            .map(integer -> CITIZEN_NAME_PREFIX + integer)
            .collect(Collectors.toList());

        for (int i = 0; i < citizenNames.size(); i++) {
            renameCitizen(driver, citizenNames.get(i), newNames.get(i));
        }

        //Order citizens
        SkyXplorePlanetPopulationOverviewActions.orderCitizens(driver, CitizenOrder.DESCENDING);
        verifyCitizenOrder(driver, CitizenOrder.DESCENDING, newNames);

        SkyXplorePlanetPopulationOverviewActions.orderCitizens(driver, CitizenOrder.ASCENDING);
        verifyCitizenOrder(driver, CitizenOrder.ASCENDING, newNames);

        //Display/Hide skills
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
