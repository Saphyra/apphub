package com.github.saphyra.apphub.integration.framework;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WebElementUtils {
    public static void clearAndFill(WebElement webElement, Object input) {
        clearAndFill(webElement, input.toString());
    }

    public static void clearAndFill(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public static void clearAndFillNotLooseFocus(WebElement webElement, String text, int oldTextLength) {
        for(int i = 0; i < oldTextLength; i++){
            webElement.sendKeys(Keys.BACK_SPACE);
        }
        webElement.sendKeys(text);
    }

    public static void setNumberSlow(WebElement webElement, Integer value) {
        Integer currentValue = WebElementUtils.getValueOfInputAs(webElement, Integer::parseInt);

        int diff = currentValue - value;

        for (int i = 0; i < Math.abs(diff); i++) {
            webElement.sendKeys(diff > 0 ? Keys.DOWN : Keys.UP);
            SleepUtil.sleep(100);
        }
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

    public static void selectOptionById(WebElement selectMenu, String id) {
        selectMenu.click();
        selectMenu.findElement(By.cssSelector(String.format(":scope #%s", id)))
            .click();
    }

    public static void setCheckboxState(WebElement webElement, boolean shouldBeChecked) {
        if (!webElement.isSelected() == shouldBeChecked) {
            webElement.click();
        }
    }

    public static void verifyInvalidFieldState(Optional<WebElement> inputValid, boolean shouldBeVisible, String errorMessage) {
        if (shouldBeVisible) {
            assertThat(inputValid).isNotEmpty();
            assertThat(inputValid.get().getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid).isEmpty();
        }
    }

    @Deprecated
    public static void verifyInvalidFieldStateLegacy(Optional<WebElement> inputValid, boolean shouldBeVisible, String errorMessage) {
        assertThat(inputValid).isNotNull();

        if (shouldBeVisible) {
            assertThat(inputValid.get().isDisplayed()).isTrue();
            assertThat(inputValid.get().getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid.get().isDisplayed()).isFalse();
        }
    }

    public static List<String> getClasses(WebElement element) {
        return Arrays.asList(element.getAttribute("class").split(" "));
    }

    public static Optional<WebElement> getIfPresent(Supplier<WebElement> search) {
        try {
            return Optional.of(search.get());
        } catch (Exception e) {
            log.debug("Error querying webElement", e);

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

    public static void clearAndFillDate(WebElement webElement, LocalDate date) {
        webElement.sendKeys(String.valueOf(date.getYear()));
        webElement.sendKeys(Keys.TAB);
        webElement.sendKeys(CommonUtils.withLeadingZeros(date.getMonthValue(), 2));
        webElement.sendKeys(CommonUtils.withLeadingZeros(date.getDayOfMonth(), 2));
    }

    public static void clearAndFillTime(WebElement webElement, Integer hours, Integer minutes) {
        webElement.sendKeys(CommonUtils.withLeadingZeros(hours, 2));
        webElement.sendKeys(CommonUtils.withLeadingZeros(minutes, 2));
    }

    public static <T> T getValueOfInputAs(WebElement webElement, Function<String, T> mapper) {
        return mapper.apply(webElement.getAttribute("value"));
    }
}
