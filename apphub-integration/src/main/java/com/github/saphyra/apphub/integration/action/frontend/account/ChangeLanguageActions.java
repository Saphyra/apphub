package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.Language;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ChangeLanguageActions {
    public static void selectLanguage(WebDriver driver, Language language) {
        driver.findElement(By.cssSelector("#language-selector ." + language.getLocale()))
            .click();
    }

    public static Language getActiveLanguage(WebDriver driver) {
        return getLanguageSelectorButtons(driver)
            .stream()
            .map(WebElementUtils::getClasses)
            .filter(classes -> classes.contains("current"))
            .findFirst()
            .map(Language::fromClasses)
            .orElseThrow(() -> new RuntimeException("Active language not found."));
    }

    private static List<WebElement> getLanguageSelectorButtons(WebDriver driver) {
        return driver.findElements(By.cssSelector("#language-selector .language"));
    }
}
