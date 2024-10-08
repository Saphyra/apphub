package com.github.saphyra.apphub.integration.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbySettingsHelper;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.Range;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.AiPlayerElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LobbySettingsTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    private static final String AI_NAME = "ai-%s";
    private static final String NEW_AI_NAME = "new-ai-name";

    @Test(groups = {"fe", "skyxplore"})
    public void allianceSettings() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(getServerPort(), List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        SkyXploreLobbyActions.findPlayerValidated(driver1, userData1.getUsername())
            .changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayerValidated(driver1, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findPlayerValidated(driver2, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        assertThat(SkyXploreLobbyActions.findPlayerValidated(driver2, userData1.getUsername()).allianceChangeEnabled()).isFalse();

        SkyXploreLobbyActions.getPlayer(driver1, userData2.getUsername())
            .changeAllianceTo(Constants.NO_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getPlayer(driver1, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getPlayer(driver2, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();
    }

    @Test(groups = {"fe", "skyxplore"})
    public void gameSettings() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(getServerPort(), List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        maxPlayersPerSolarSystemsSettings(driver1, driver2);
        additionalSolarSystemsSettings(driver1, driver2);
        planetsPerSolarSystemSettings(driver1, driver2);
        planetSizeSettings(driver1, driver2);
    }

    private void maxPlayersPerSolarSystemsSettings(WebDriver driver1, WebDriver driver2) {
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
    }

    private void additionalSolarSystemsSettings(WebDriver driver1, WebDriver driver2) {
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
    }

    private void planetsPerSolarSystemSettings(WebDriver driver1, WebDriver driver2) {
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
    }

    private void planetSizeSettings(WebDriver driver1, WebDriver driver2) {
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

    @Test(groups = {"fe", "skyxplore"})
    public void ais() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(getServerPort(), List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        aiValidation(driver1);
        createAndRemoveAi(driver1, driver2);
        setAlliance(driver1, driver2);
        renameAi(driver1, driver2);
    }

    private static void aiValidation(WebDriver driver1) {
        SkyXploreLobbyActions.fillNewAiName(driver1, "aa");
        SkyXploreLobbyActions.verifyInvalidAiName(driver1, "Character name too short. (Minimum 3 characters)");

        SkyXploreLobbyActions.fillNewAiName(driver1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreLobbyActions.verifyInvalidAiName(driver1, "Character name too long. (Maximum 30 characters)");
    }

    private void createAndRemoveAi(WebDriver driver1, WebDriver driver2) {
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
    }

    private static void setAlliance(WebDriver driver1, WebDriver driver2) {
        SkyXploreLobbyActions.createAi(driver1, AI_NAME);

        AiPlayerElement aiPlayer = AwaitilityWrapper.getWithWait(() -> SkyXploreLobbyActions.findAiByName(driver1, AI_NAME), Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new RuntimeException("AiPlayer not found with name " + AI_NAME));

        aiPlayer.setAlliance(Constants.NEW_ALLIANCE_LABEL);

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

    private void renameAi(WebDriver driver1, WebDriver driver2) {
        SkyXploreLobbyActions.findAiByNameValidated(driver1, AI_NAME)
            .rename(NEW_AI_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.findAiByName(driver2, NEW_AI_NAME).isPresent())
            .assertTrue("Ai is not renamed");
    }

    @Test(groups = {"fe", "skyxplore"})
    void gameDoesNotStartWithOnlyOneAlliance() {
        WebDriver driver = extractDriver();

        RegistrationParameters userData = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);
        SkyXploreCharacterActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue();

        SkyXploreMainMenuActions.createLobby(driver, GAME_NAME);

        SkyXploreLobbyActions.createAi(driver, AI_NAME);

        SkyXploreLobbyActions.findPlayerValidated(driver, userData.getUsername())
            .changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        SkyXploreLobbyActions.findAiByName(driver, AI_NAME)
            .orElseThrow()
            .setAlliance("1");

        SkyXploreLobbyActions.setReady(driver);

        SkyXploreLobbyActions.startGameCreation(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_LOBBY_ONLY_ONE_ALLIANCE);
    }
}
