package com.github.saphyra.apphub.integration.frontend.framework;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class WebElementUtils {
    public static void clearAndFill(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public static void clearAndFillContentEditable(WebDriver driver, WebElement webElement, String text) {
        Actions navigator = new Actions(driver);
        navigator.click(webElement)
            .sendKeys(Keys.END)
            .keyDown(Keys.SHIFT)
            .sendKeys(Keys.HOME)
            .keyUp(Keys.SHIFT)
            .sendKeys(Keys.BACK_SPACE);

        for (String s : text.split("")) {
            navigator.sendKeys(s);
        }

        navigator.perform();
    }

    public static void selectOption(WebElement selectMenu, String value) {
        selectMenu.click();
        selectMenu.findElement(By.cssSelector(String.format(":scope option[value=\"%s\"]", value)))
            .click();
    }

    public static void setCheckboxState(WebElement webElement, boolean shouldBeChecked) {
        if (!webElement.isSelected() == shouldBeChecked) {
            webElement.click();
        }
    }

    public static void verifyInvalidFieldState(WebElement inputValid, boolean shouldBeVisible, String errorMessage) {
        if (shouldBeVisible) {
            assertThat(inputValid.isDisplayed()).isTrue();
            assertThat(inputValid.getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid.isDisplayed()).isFalse();
        }
    }

    public static List<String> getClasses(WebElement element) {
        return Arrays.asList(element.getAttribute("class").split(" "));
    }

    public static Optional<WebElement> getIfPresent(Supplier<WebElement> search) {
        try {
            return Optional.of(search.get());
        } catch (Exception e) {
            return Optional.empty();
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

    public static void verifyInvalidFieldStateSoft(WebElement inputValid, boolean shouldBeVisible, String errorMessage) {
        if (shouldBeVisible) {
            assertThat(inputValid.isDisplayed()).isTrue();
            assertThat(inputValid.getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid.isDisplayed()).isFalse();
        }
    }
}
