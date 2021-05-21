package com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Invitation;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

@Slf4j
public class SkyXploreMainMenuActions {
    public static void back(WebDriver driver) {
        MainMenuPage.backButton(driver).click();
    }

    public static void createLobby(WebDriver driver, String gameName) {
        log.info("Creating lobby with name {}", gameName);

        openCreateGameDialog(driver);
        fillGameName(driver, gameName);
        submitGameCreationForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.pageLoaded(driver))
            .assertTrue("LobbyPage not loaded.");
    }

    public static void editCharacter(WebDriver driver) {
        MainMenuPage.editCharacterButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXploreCharacterActions.getBoxTitle(driver).isEmpty());
    }

    public static void openCreateGameDialog(WebDriver driver) {
        MainMenuPage.createGameButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> MainMenuPage.createGameDialog(driver).isDisplayed())
            .assertTrue("Failed opening Game creation dialog");
    }

    public static void fillGameName(WebDriver driver, String gameName) {
        clearAndFill(MainMenuPage.gameNameInput(driver), gameName);
    }

    public static void verifyInvalidGameName(WebDriver driver, String errorMessage) {
        WebElementUtils.verifyInvalidFieldStateSoft(MainMenuPage.invalidGameName(driver), true, errorMessage);
        TestBase.getSoftAssertions().assertThat(MainMenuPage.submitGameCreationFormButton(driver).isEnabled()).isFalse();
    }

    public static void waitForPageLoads(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> !driver.findElements(MainMenuPage.GAME_NAME_INPUT).isEmpty())
            .assertTrue("Failed to load SkyXplore MainMenu page.");
    }

    public static void verifyValidGameName(WebDriver driver) {
        WebElementUtils.verifyInvalidFieldStateSoft(MainMenuPage.invalidGameName(driver), false, null);
        TestBase.getSoftAssertions().assertThat(MainMenuPage.submitGameCreationFormButton(driver).isEnabled()).isTrue();
    }

    public static void submitGameCreationForm(WebDriver driver) {
        WebElement submitButton = MainMenuPage.submitGameCreationFormButton(driver);
        AwaitilityWrapper.createDefault()
            .until(submitButton::isEnabled)
            .assertTrue("Game creation form is invalid.");

        submitButton.click();
    }

    public static List<Invitation> getInvitations(WebDriver driver) {
        return MainMenuPage.invitations(driver)
            .stream()
            .map(Invitation::new)
            .collect(Collectors.toList());
    }

    public static void acceptInvitation(WebDriver driver, String username) {
        AwaitilityWrapper.getListWithWait(() -> getInvitations(driver), invitations -> !invitations.isEmpty())
            .stream()
            .filter(invitation -> invitation.getInvitor().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Invitation not found"))
            .accept();
    }
}
