package com.github.saphyra.apphub.integraton.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbySettingsHelper;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.Range;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbySettingsTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String AI_NAME = "ai-%s";

    @Test(groups = "skyxplore")
    public void allianceSettings() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        List<Future<Object>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(player -> EXECUTOR_SERVICE.submit(() -> {
                Navigation.toIndexPage(player.getEntity1());
                IndexPageActions.registerUser(player.getEntity1(), player.getEntity2());
                ModulesPageActions.openModule(player.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(player.getEntity1());

                AwaitilityWrapper.createDefault()
                    .until(() -> player.getEntity1().getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue();
                return null;
            }))
            .toList();

        AwaitilityWrapper.create(120, 1)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue("Players not created.");

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        //Alliance settings
        SkyXploreLobbyActions.findMemberValidated(driver1, userData1.getUsername())
            .changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findMemberValidated(driver1, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findMemberValidated(driver2, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        assertThat(SkyXploreLobbyActions.findMemberValidated(driver2, userData1.getUsername()).allianceChangeEnabled()).isFalse();

        SkyXploreLobbyActions.getMember(driver1, userData2.getUsername())
            .changeAllianceTo(Constants.NO_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver1, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver2, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();
    }

    @Test(groups = "skyxplore")
    public void gameSettings() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        List<Future<Object>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(player -> EXECUTOR_SERVICE.submit(() -> {
                Navigation.toIndexPage(player.getEntity1());
                IndexPageActions.registerUser(player.getEntity1(), player.getEntity2());
                ModulesPageActions.openModule(player.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(player.getEntity1());

                AwaitilityWrapper.createDefault()
                    .until(() -> player.getEntity1().getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue();
                return null;
            }))
            .toList();

        AwaitilityWrapper.create(120, 1)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue("Players not created.");

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        //MaxPlayersPerSolarSystems
        setAndVerifyLobbySettings(
            "maxPlayersPerSolarSystems cannot be lower than 1",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withMaxPlayersPerSolarSystem(0),
            SkyXploreLobbySettingsHelper.withMaxPlayersPerSolarSystem(1)
        );

        setAndVerifyLobbySettings(
            "maxPlayersPerSolarSystems cannot be greater than 5",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withMaxPlayersPerSolarSystem(6),
            SkyXploreLobbySettingsHelper.withMaxPlayersPerSolarSystem(5)
        );

        //additionalSolarSystems
        setAndVerifyLobbySettings(
            "additionalSolarSystems.min must not be negative",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(-1, 2)),
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(0, 2))
        );

        setAndVerifyLobbySettings(
            "additionalSolarSystems.min must not be greater than max",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(3, 2)),
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(2, 2))
        );

        setAndVerifyLobbySettings(
            "additionalSolarSystems.max must not be lower than min",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(2, 1)),
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(2, 2))
        );

        setAndVerifyLobbySettings(
            "additionalSolarSystems.max must not be greater than 30",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(2, 31)),
            SkyXploreLobbySettingsHelper.withAdditionalSolarSystems(new Range<>(2, 30))
        );

        //planetsPerSolarSystem
        setAndVerifyLobbySettings(
            "planetsPerSolarSystem.min must not be negative",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(-1, 2)),
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(0, 2))
        );

        setAndVerifyLobbySettings(
            "planetsPerSolarSystem.min must not be greater than max",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(4, 3)),
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(3, 3))
        );

        setAndVerifyLobbySettings(
            "planetsPerSolarSystem.max must not be greater than min",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(3, 2)),
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(3, 3))
        );

        setAndVerifyLobbySettings(
            "planetsPerSolarSystem.max must not be greater than 10",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(0, 11)),
            SkyXploreLobbySettingsHelper.withPlanetsPerSolarSystem(new Range<>(0, 10))
        );

        //planetSize
        setAndVerifyLobbySettings(
            "planetSize.min must not be lower than 10",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(9, 15)),
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(10, 15))
        );

        setAndVerifyLobbySettings(
            "planetSize.min must not be greater than max",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(16, 15)),
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(15, 15))
        );

        setAndVerifyLobbySettings(
            "planetSize.max must not be lower than min",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(15, 14)),
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(15, 15))
        );

        setAndVerifyLobbySettings(
            "planetSize.min must not be greater than 20",
            driver1,
            driver2,
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(15, 21)),
            SkyXploreLobbySettingsHelper.withPlanetSize(new Range<>(15, 20))
        );
    }

    private void setAndVerifyLobbySettings(String testCase, WebDriver hostDriver, WebDriver memberDriver, SkyXploreGameSettings toSet, SkyXploreGameSettings shouldBeVisible) {
        try {
            SkyXploreLobbyActions.setSettings(hostDriver, toSet);

            SkyXploreLobbyActions.verifySettings(hostDriver, shouldBeVisible);
            SkyXploreLobbyActions.verifySettings(memberDriver, shouldBeVisible);
        } catch (Throwable e) {
            throw new RuntimeException(testCase + " failed.", e);
        }
    }

    @Test(groups = "skyxplore")
    public void ais() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        List<Future<Object>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(player -> EXECUTOR_SERVICE.submit(() -> {
                Navigation.toIndexPage(player.getEntity1());
                IndexPageActions.registerUser(player.getEntity1(), player.getEntity2());
                ModulesPageActions.openModule(player.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(player.getEntity1());

                AwaitilityWrapper.createDefault()
                    .until(() -> player.getEntity1().getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue();
                return null;
            }))
            .toList();

        AwaitilityWrapper.create(120, 1)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue("Players not created.");

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        //Validation
        SkyXploreLobbyActions.fillNewAiName(driver1, "aa");
        SkyXploreLobbyActions.verifyInvalidAiName(driver1, "Karakter név túl rövid. (Minimum 3 karakter)");

        SkyXploreLobbyActions.fillNewAiName(driver1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreLobbyActions.verifyInvalidAiName(driver1, "Karakter név túl hosszú. (Maximum 30 karakter)");

        //Create and Remove
        assertThat(SkyXploreLobbyActions.isCreateAiPanelPresent(driver2)).isFalse();

        Stream.iterate(0, integer -> integer + 1)
            .limit(10)
            .map(integer -> String.format(AI_NAME, integer))
            .forEach(aiName -> createAi(driver1, driver2, aiName));

        assertThat(SkyXploreLobbyActions.isCreateAiPanelPresent(driver1)).isFalse();

        Stream.iterate(0, integer -> integer + 1)
            .limit(10)
            .map(integer -> String.format(AI_NAME, integer))
            .forEach(aiName -> removeAi(driver1, driver2, aiName));

        //Set alliance
        SkyXploreLobbyActions.createAi(driver1, AI_NAME);

        AiPlayer aiPlayer = AwaitilityWrapper.getWithWait(() -> SkyXploreLobbyActions.findAiByName(driver1, AI_NAME), Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new RuntimeException("AiPlayer not found with name " + AI_NAME));

        aiPlayer.setAlliance("Új szövetség");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByNameValidated(driver1, AI_NAME).getAlliance().equals("1"))
            .assertTrue("Alliance is not changed.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByNameValidated(driver2, AI_NAME).getAlliance().equals("1"))
            .assertTrue("Alliance is not changed.");
    }

    private void createAi(WebDriver hostDriver, WebDriver playerDriver, String aiName) {
        SkyXploreLobbyActions.createAi(hostDriver, aiName);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByName(hostDriver, aiName).isPresent())
            .assertTrue("Ai not found with name " + aiName);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByName(playerDriver, aiName).isPresent())
            .assertTrue("Ai not found with name " + aiName);
    }

    private void removeAi(WebDriver hostDriver, WebDriver playerDriver, String aiName) {
        SkyXploreLobbyActions.findAiByName(hostDriver, aiName)
            .orElseThrow(() -> new RuntimeException("Ai not found with name " + aiName))
            .remove();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByName(hostDriver, aiName).isEmpty())
            .assertTrue("Ai with name " + aiName + " is still present.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByName(playerDriver, aiName).isEmpty())
            .assertTrue("Ai with name " + aiName + " is still present.");
    }
}
