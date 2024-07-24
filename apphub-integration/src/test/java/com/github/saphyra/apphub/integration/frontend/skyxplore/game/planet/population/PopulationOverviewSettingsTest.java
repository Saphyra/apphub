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
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenOrder;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkillType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.OrderType;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.StatType;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PopulationOverviewSettingsTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void saveHiddenPropertiesAsDefault() {
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

        SkyXplorePlanetActions.openPopulationOverview(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePopulationOverviewActions.isLoaded(driver))
            .assertTrue("PopulationOverview is not opened.");

        //Set hidden skills as defaults
        SkyXplorePopulationOverviewActions.toggleSkillDisplay(driver, Constants.SKILL_AIMING);
        SkyXplorePopulationOverviewActions.saveHideAsGlobalDefault(driver);
        SkyXplorePopulationOverviewActions.toggleSkillDisplay(driver, Constants.SKILL_BUILDING);
        SkyXplorePopulationOverviewActions.saveHideAsPlanetDefault(driver);
        SkyXplorePopulationOverviewActions.close(driver);

        //Check if planet defaults loaded
        SkyXplorePlanetActions.openPopulationOverview(driver);

        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_AIMING)).isFalse();
        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_BUILDING)).isFalse();

        //Delete planet defaults
        SkyXplorePopulationOverviewActions.deleteHiddenPlanetDefault(driver);

        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_AIMING)).isFalse();
        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_BUILDING)).isTrue();

        //Delete global defaults
        SkyXplorePopulationOverviewActions.deleteHiddenGlobalDefault(driver);

        SkyXplorePopulationOverviewActions.close(driver);
        SkyXplorePlanetActions.openPopulationOverview(driver);

        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_AIMING)).isTrue();
        assertThat(SkyXplorePopulationOverviewActions.isSkillDisplayed(driver, Constants.SKILL_BUILDING)).isTrue();
    }

    @Test(groups = {"fe", "skyxplore"})
    public void saveOrderPropertiesAsDefault() {
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

        SkyXplorePlanetActions.openPopulationOverview(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePopulationOverviewActions.isLoaded(driver))
            .assertTrue("PopulationOverview is not opened.");

        //Set orders as defaults
        SkyXplorePopulationOverviewActions.orderCitizens(driver, CitizenOrder.ASCENDING);
        SkyXplorePopulationOverviewActions.orderCitizensBy(driver, OrderType.BY_STAT);
        SkyXplorePopulationOverviewActions.selectStatType(driver, StatType.SATIETY);
        SkyXplorePopulationOverviewActions.saveOrderAsGlobalDefault(driver);

        SkyXplorePopulationOverviewActions.orderCitizens(driver, CitizenOrder.DESCENDING);
        SkyXplorePopulationOverviewActions.orderCitizensBy(driver, OrderType.BY_SKILL);
        SkyXplorePopulationOverviewActions.selectSkillType(driver, SkillType.MELEE_FIGHTING);
        SkyXplorePopulationOverviewActions.saveOrderAsPlanetDefault(driver);

        SkyXplorePopulationOverviewActions.close(driver);

        //Check if planet setting loaded
        SkyXplorePlanetActions.openPopulationOverview(driver);

        assertThat(SkyXplorePopulationOverviewActions.getSelectedOrder(driver)).isEqualTo(CitizenOrder.DESCENDING);
        assertThat(SkyXplorePopulationOverviewActions.getOrderType(driver)).isEqualTo(OrderType.BY_SKILL);
        assertThat(SkyXplorePopulationOverviewActions.getSelectedSkill(driver)).isEqualTo(SkillType.MELEE_FIGHTING);

        //Delete planet default
        SkyXplorePopulationOverviewActions.deleteOrderPlanetDefault(driver);

        AwaitilityWrapper.awaitAssert(() -> SkyXplorePopulationOverviewActions.getSelectedOrder(driver), citizenOrder -> assertThat(citizenOrder).isEqualTo(CitizenOrder.ASCENDING));

        assertThat(SkyXplorePopulationOverviewActions.getOrderType(driver)).isEqualTo(OrderType.BY_STAT);
        assertThat(SkyXplorePopulationOverviewActions.getSelectedStat(driver)).isEqualTo(StatType.SATIETY);

        //Delete global default
        SkyXplorePopulationOverviewActions.deleteOrderGlobalDefault(driver);
        SkyXplorePopulationOverviewActions.close(driver);
        SkyXplorePlanetActions.openPopulationOverview(driver);

        assertThat(SkyXplorePopulationOverviewActions.getSelectedOrder(driver)).isEqualTo(CitizenOrder.ASCENDING);
        assertThat(SkyXplorePopulationOverviewActions.getOrderType(driver)).isEqualTo(OrderType.BY_NAME);
    }
}
