package com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.skyxplore.AiPlayerElement;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyChatMessage;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyMember;
import com.github.saphyra.apphub.integration.structure.skyxplore.OnlineFriend;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreGameSettings;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.setNumberSlow;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SkyXploreLobbyActions {
    public static void startGameCreation(WebDriver driver) {
        LobbyPage.startGameCreationButton(driver).click();
    }

    public static void setReady(WebDriver driver) {
        LobbyPage.setReadyButton(driver).click();
    }

    public static boolean pageLoaded(WebDriver driver) {
        return !getSystemMessages(driver).isEmpty();
    }

    public static List<String> getSystemMessages(WebDriver driver) {
        return LobbyPage.systemMessages(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    public static List<OnlineFriend> getOnlineFriends(WebDriver driver) {
        return LobbyPage.onlineFriends(driver)
            .stream()
            .map(OnlineFriend::new)
            .collect(Collectors.toList());
    }

    public static LobbyMember findMemberValidated(WebDriver driver, String name) {
        return findMember(driver, name)
            .orElseThrow(() -> new RuntimeException("LobbyMember not found with name " + name));
    }

    public static Optional<LobbyMember> findMember(WebDriver driver, String name) {
        return getMembers(driver)
            .stream()
            .filter(lobbyMember -> lobbyMember.getName().equals(name))
            .findFirst();
    }

    public static List<LobbyMember> getMembers(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> LobbyPage.lobbyMembers(driver), webElements -> !webElements.isEmpty())
            .stream()
            .map(LobbyMember::new)
            .collect(Collectors.toList());
    }

    public static void inviteFriend(WebDriver driver, String username) {
        getOnlineFriend(driver, username)
            .invite();
    }

    public static OnlineFriend getOnlineFriend(WebDriver driver, String username) {
        return AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getOnlineFriends(driver), webElements -> !webElements.isEmpty())
            .stream()
            .filter(element -> element.getName().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Online friend not found."));
    }

    public static void sendMessage(WebDriver driver, String message) {
        WebElement chatInput = LobbyPage.chatInput(driver);

        clearAndFill(chatInput, message);

        chatInput.sendKeys(Keys.RETURN);
    }

    public static List<LobbyChatMessage> getMessages(WebDriver driver) {
        return LobbyPage.messages(driver)
            .stream()
            .map(LobbyChatMessage::new)
            .collect(Collectors.toList());
    }

    public static void exitLobby(WebDriver driver) {
        LobbyPage.exitButton(driver).click();
    }

    public static LobbyMember getMember(WebDriver driver, String username) {
        return findMember(driver, username)
            .orElseThrow(() -> new RuntimeException("LobbyMember not found."));
    }

    public static void startGameWithMissingPlayers(WebDriver driver) {
        LobbyPage.startGameAnywaysButton(driver)
            .click();
    }

    public static void setSettings(WebDriver driver, SkyXploreGameSettings settings) {
        setNumberSlow(LobbyPage.maxPlayersPerSolarSystem(driver), settings.getMaxPlayersPerSolarSystem());

        setNumberSlow(LobbyPage.additionalSolarSystemsMax(driver), settings.getAdditionalSolarSystems().getMax());
        setNumberSlow(LobbyPage.additionalSolarSystemsMin(driver), settings.getAdditionalSolarSystems().getMin());

        setNumberSlow(LobbyPage.planetsPerSolarSystemMax(driver), settings.getPlanetsPerSolarSystem().getMax());
        setNumberSlow(LobbyPage.planetsPerSolarSystemMin(driver), settings.getPlanetsPerSolarSystem().getMin());

        setNumberSlow(LobbyPage.planetSizeMax(driver), settings.getPlanetSize().getMax());
        setNumberSlow(LobbyPage.planetSizeMin(driver), settings.getPlanetSize().getMin());
    }

    public static void verifySettings(WebDriver driver, SkyXploreGameSettings shouldBeVisible) {
        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.maxPlayersPerSolarSystem(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getMaxPlayersPerSolarSystem());

        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.additionalSolarSystemsMin(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getAdditionalSolarSystems().getMin());
        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.additionalSolarSystemsMax(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getAdditionalSolarSystems().getMax());

        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.planetsPerSolarSystemMin(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getPlanetsPerSolarSystem().getMin());
        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.planetsPerSolarSystemMax(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getPlanetsPerSolarSystem().getMax());

        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.planetSizeMin(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getPlanetSize().getMin());
        assertThat((Integer) WebElementUtils.getValueOfInputAs(LobbyPage.planetSizeMax(driver), Integer::parseInt)).isEqualTo(shouldBeVisible.getPlanetSize().getMax());
    }

    public static void createAi(WebDriver driver, String aiName) {
        fillNewAiName(driver, aiName);

        LobbyPage.createAiButton(driver)
            .click();
    }

    public static void fillNewAiName(WebDriver driver, String aiName) {
        clearAndFill(LobbyPage.newAiName(driver), aiName);
    }

    public static void verifyInvalidAiName(WebDriver driver, String errorMessage) {
        WebElementUtils.verifyInvalidFieldState(LobbyPage.invalidNewAiName(driver), true, errorMessage);
        assertThat(LobbyPage.createAiButton(driver).isEnabled()).isFalse();
    }

    public static AiPlayerElement findAiByNameValidated(WebDriver driver, String aiName) {
        return findAiByName(driver, aiName)
            .orElseThrow(() -> new RuntimeException("AiPlayer not found with name " + aiName));
    }

    public static Optional<AiPlayerElement> findAiByName(WebDriver driver, String aiName) {
        return getAis(driver)
            .stream()
            .filter(aiPlayer -> aiPlayer.getName().equals(aiName))
            .findFirst();
    }

    public static List<AiPlayerElement> getAis(WebDriver driver) {
        return LobbyPage.getAis(driver)
            .stream()
            .map(AiPlayerElement::new)
            .collect(Collectors.toList());
    }

    public static boolean isCreateAiPanelPresent(WebDriver driver) {
        return LobbyPage.createAiPanel(driver)
            .isPresent();
    }
}
