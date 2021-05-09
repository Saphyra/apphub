package com.github.saphyra.apphub.integration.frontend.service.skyxplore.character;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

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
        WebElementUtils.verifyInvalidFieldStateSoft(CharacterPage.invalidCharacterName(driver), true, errorMessage);
        TestBase.getSoftAssertions().assertThat(CharacterPage.submitButton(driver).isEnabled()).isFalse();
    }

    public static void verifyValidCharacterName(WebDriver driver) {
        WebElementUtils.verifyInvalidFieldStateSoft(CharacterPage.invalidCharacterName(driver), false, null);
        TestBase.getSoftAssertions().assertThat(CharacterPage.submitButton(driver).isEnabled()).isTrue();
    }

    public static void submitForm(WebDriver driver) {
        CharacterPage.submitButton(driver).click();
    }
}
