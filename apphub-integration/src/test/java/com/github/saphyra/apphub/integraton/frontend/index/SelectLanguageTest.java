package com.github.saphyra.apphub.integraton.frontend.index;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectLanguageTest extends SeleniumTest {
    @Test(groups = {"fe", "index"})
    public void selectLanguageOnIndexPage() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        Arrays.stream(Language.values())
            .forEach(language -> verify(driver, language));
    }

    private void verify(WebDriver driver, Language language) {
        driver.findElement(By.cssSelector("#language-selector .language." + language.getLanguage()))
            .click();

        SleepUtil.sleep(1000);

        assertThat(driver.findElement(By.id("registration-username")).getAttribute("placeholder")).isEqualTo(language.getLocalization());
    }

    @RequiredArgsConstructor
    @Getter
    private enum Language {
        HU("hu", "Felhasználónév"),
        EN("en", "Username");

        private final String language;
        private final String localization;
    }
}
