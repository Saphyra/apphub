package com.github.saphyra.apphub.integration.action.frontend.skyxplore.character;

import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreCharacterActions {
    public static String getCharacterName(WebDriver driver) {
        return CharacterPage.characterName(driver).getAttribute("value");
    }

    public static String getBoxTitle(WebDriver driver) {
        return CharacterPage.getBoxTitle(driver).getText();
    }

    public static void fillCharacterName(WebDriver driver, String characterName) {
        clearAndFill(CharacterPage.characterName(driver), characterName);
    }

    public static void verifyInvalidCharacterName(WebDriver driver, String errorMessage) {
        WebElementUtils.verifyInvalidFieldState(CharacterPage.invalidCharacterName(driver), true, errorMessage);
        assertThat(CharacterPage.submitButton(driver).isEnabled()).isFalse();
    }

    public static void verifyValidCharacterName(WebDriver driver) {
        WebElementUtils.verifyInvalidFieldStateLegacy(CharacterPage.invalidCharacterName(driver), false, null);
        assertThat(CharacterPage.submitButton(driver).isEnabled()).isTrue();
    }

    public static void submitForm(WebDriver driver) {
        CharacterPage.submitButton(driver).click();
    }

    public static void createCharacter(WebDriver driver) {
        submitForm(driver);
        NotificationUtil.verifySuccessNotification(driver, "Karakter elmentve.");
    }
}
