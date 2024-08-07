package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.localization.LocalizedText;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ToastMessageUtil {
    public static void verifyErrorToast(WebDriver driver, LocalizedText localizedText) {
        verifyErrorToast(driver, localizedText.getText());
    }

    public static void verifySuccessToast(WebDriver driver, LocalizedText localizedText) {
        verifySuccessToast(driver, localizedText.getText());
    }

    private static void verifyErrorToast(WebDriver driver, String message) {
        AwaitilityWrapper.create(3, 1)
            .until(() -> !getErrorToasts(driver, message).isEmpty())
            .assertTrue("No error toast found with message " + message);
    }

    private static void verifySuccessToast(WebDriver driver, String message) {
        AwaitilityWrapper.create(3, 1)
            .until(() -> !getSuccessToasts(driver, message).isEmpty())
            .assertTrue("No success toast found with message " + message);
    }

    private static List<WebElement> getErrorToasts(WebDriver driver, String message) {
        return getAllToasts(driver)
            .stream()
            .peek(webElement -> log.debug("All toasts: {}", webElement))
            .filter(webElement -> WebElementUtils.getClasses(webElement).contains("Toastify__toast--error"))
            .peek(webElement -> log.debug("Error toasts: {}", webElement))
            .map(webElement -> webElement.findElement(By.cssSelector(":scope .Toastify__toast-body > div:last-child")))
            .peek(webElement -> log.debug("Error toast messages: {}", webElement.getText()))
            .filter(webElement -> webElement.getText().equals(message))
            .peek(webElement -> log.debug("Matching error toasts: {}", webElement))
            .peek(WebElement::click)
            .toList();
    }

    private static List<WebElement> getSuccessToasts(WebDriver driver, String message) {
        return getAllToasts(driver)
            .stream()
            .peek(webElement -> log.debug("All toasts: {}", webElement))
            .filter(webElement -> WebElementUtils.getClasses(webElement).contains("Toastify__toast--success"))
            .peek(webElement -> log.debug("Success toasts: {}", webElement))
            .map(webElement -> webElement.findElement(By.cssSelector(":scope .Toastify__toast-body > div:last-child")))
            .peek(webElement -> log.debug("Success toast messages: {}", webElement.getText()))
            .filter(webElement -> webElement.getText().equals(message))
            .peek(webElement -> log.debug("Matching success toasts: {}", webElement))
            .peek(WebElement::click)
            .toList();
    }

    private static List<WebElement> getAllToasts(WebDriver driver) {
        return driver.findElements(By.className("Toastify__toast"));
    }

    public static void verifyNoNotifications(WebDriver driver) {
        assertThat(getAllToasts(driver)).isEmpty();
    }

    public static void clearToasts(WebDriver driver) {
        try {
            getAllToasts(driver)
                .forEach(webElement -> webElement.findElement(By.tagName("button")).click());
        } catch (StaleElementReferenceException e) {
            log.debug("Failed clearing toasts.", e);
        }
    }
}
