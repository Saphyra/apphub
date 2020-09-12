package com.github.saphyra.apphub.integration.frontend.framework;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class WebElementUtils {

    public static void clearAndFill(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public static void verifyInvalidFieldState(WebElement inputValid, boolean shouldBeVisible, String errorMessage) {
        if (shouldBeVisible) {
            assertThat(inputValid.isDisplayed()).isTrue();
            assertThat(inputValid.getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid.isDisplayed()).isFalse();
        }
    }

    public static boolean isStale(WebElement element) {
        try {
            element.isDisplayed();
            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }
}
