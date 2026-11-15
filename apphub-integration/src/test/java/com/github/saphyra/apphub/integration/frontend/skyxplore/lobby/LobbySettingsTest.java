package com.github.saphyra.apphub.integration.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyPageActions;
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

    /**
     * Tests alliance and game settings validation in a lobby.
     *
     * <ul>
     *   <li>Host changes own alliance; member sees updated alliance.</li>
     *   <li>Member cannot change alliance after host changed.</li>
     *   <li>Changing another player's alliance updates both hosts and members.</li>
     *   <li>Lobby settings (max players, additional solar systems, planets per system, planet size) enforce min/max constraints.</li>
     * </ul>
     */
    @Test(groups = {"fe", "skyxplore"})
    public void allianceSettings() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(getServerPort(), List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        SkyXploreLobbyPageActions.findPlayerValidated(driver1, userData1.getUsername())
            .changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findPlayerValidated(driver1, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findPlayerValidated(driver2, userData1.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        assertThat(SkyXploreLobbyPageActions.findPlayerValidated(driver2, userData1.getUsername()).allianceChangeEnabled()).isFalse();

        SkyXploreLobbyPageActions.getPlayer(driver1, userData2.getUsername())
            .changeAllianceTo(Constants.NO_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.getPlayer(driver1, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.getPlayer(driver2, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_LABEL))
            .assertTrue();
    }

    /**
     * Tests validation of lobby game settings for various parameters.
     *
     * <ul>
     *   <li>maxPlayersPerSolarSystems must be between 1 and 5.</li>
     *   <li>additionalSolarSystems range min >=0, max <=30, min <= max.</li>
     *   <li>planetsPerSolarSystem range min >=0, max <=10, min <= max.</li>
     *   <li>planetSize range min >=10, max <=20, min <= max.</li>
     * </ul>
     */
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
            SkyXploreLobbyPageActions.setSettings(hostDriver, toSet);

            SkyXploreLobbyPageActions.verifySettings(hostDriver, shouldBeVisible);
            SkyXploreLobbyPageActions.verifySettings(memberDriver, shouldBeVisible);
        } catch (Throwable e) {
            throw new RuntimeException(testCase + " failed.", e);
        }
    }

    /**
     * Tests AI creation, validation, removal, alliance assignment and renaming in a lobby.
     *
     * <ul>
     *   <li>AI name too short or too long triggers validation errors.</li>
     *   <li>Host can create 10 AIs; all are present for host and member.</li>
     *   <li>Host can remove 10 AIs; all are removed from both hosts and members.</li>
     *   <li>AI alliance can be set by host and is reflected on member side.</li>
     *   <li>AI can be renamed; new name appears for both hosts and members.</li>
     * </ul>
     */
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
        SkyXploreLobbyPageActions.fillNewAiName(driver1, "aa");
        SkyXploreLobbyPageActions.verifyInvalidAiName(driver1, "Character name too short. (Minimum 3 characters)");

        SkyXploreLobbyPageActions.fillNewAiName(driver1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreLobbyPageActions.verifyInvalidAiName(driver1, "Character name too long. (Maximum 30 characters)");
    }

    private void createAndRemoveAi(WebDriver driver1, WebDriver driver2) {
        assertThat(SkyXploreLobbyPageActions.isCreateAiPanelPresent(driver2)).isFalse();

        Stream.iterate(0, integer -> integer + 1)
            .limit(10)
            .map(integer -> String.format(AI_NAME, integer))
            .forEach(aiName -> createAi(driver1, driver2, aiName));

        assertThat(SkyXploreLobbyPageActions.isCreateAiPanelPresent(driver1)).isFalse();

        Stream.iterate(0, integer -> integer + 1)
            .limit(10)
            .map(integer -> String.format(AI_NAME, integer))
            .forEach(aiName -> removeAi(driver1, driver2, aiName));
    }

    private static void setAlliance(WebDriver driver1, WebDriver driver2) {
        SkyXploreLobbyPageActions.createAi(driver1, AI_NAME);

        AiPlayerElement aiPlayer = AwaitilityWrapper.getWithWait(() -> SkyXploreLobbyPageActions.findAiByName(driver1, AI_NAME), Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new RuntimeException("AiPlayer not found with name " + AI_NAME));

        aiPlayer.setAlliance(Constants.NEW_ALLIANCE_LABEL);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByNameValidated(driver1, AI_NAME).getAlliance().equals("1"))
            .assertTrue("Alliance is not changed.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByNameValidated(driver2, AI_NAME).getAlliance().equals("1"))
            .assertTrue("Alliance is not changed.");
    }

    private void createAi(WebDriver hostDriver, WebDriver playerDriver, String aiName) {
        SkyXploreLobbyPageActions.createAi(hostDriver, aiName);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByName(hostDriver, aiName).isPresent())
            .assertTrue("Ai not found with name " + aiName);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByName(playerDriver, aiName).isPresent())
            .assertTrue("Ai not found with name " + aiName);
    }

    private void removeAi(WebDriver hostDriver, WebDriver playerDriver, String aiName) {
        SkyXploreLobbyPageActions.findAiByName(hostDriver, aiName)
            .orElseThrow(() -> new RuntimeException("Ai not found with name " + aiName))
            .remove();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByName(hostDriver, aiName).isEmpty())
            .assertTrue("Ai with name " + aiName + " is still present.");

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByName(playerDriver, aiName).isEmpty())
            .assertTrue("Ai with name " + aiName + " is still present.");
    }

    private void renameAi(WebDriver driver1, WebDriver driver2) {
        SkyXploreLobbyPageActions.findAiByNameValidated(driver1, AI_NAME)
            .rename(NEW_AI_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyPageActions.findAiByName(driver2, NEW_AI_NAME).isPresent())
            .assertTrue("Ai is not renamed");
    }

    /**
     * Tests that a lobby cannot start if only one alliance is present.
     *
     * <ul>
     *   <li>Host creates lobby, adds AI and changes player to new alliance; attempts to start game.</li>
     *   <li>Error toast "Only one alliance" appears.</li>
     * </ul>
     */
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

        SkyXploreLobbyPageActions.createAi(driver, AI_NAME);

        SkyXploreLobbyPageActions.findPlayerValidated(driver, userData.getUsername())
            .changeAllianceTo(Constants.NEW_ALLIANCE_LABEL);

        SkyXploreLobbyPageActions.findAiByName(driver, AI_NAME)
            .orElseThrow()
            .setAlliance("1");

        SkyXploreLobbyPageActions.setReady(driver);

        SkyXploreLobbyPageActions.startGameCreation(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_LOBBY_ONLY_ONE_ALLIANCE);
    }
}
