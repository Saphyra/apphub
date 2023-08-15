package com.github.saphyra.apphub.integration.framework;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class ToastMessageUtil {
    public static void verifyErrorToast(WebDriver driver, String message) {
        AwaitilityWrapper.create(5, 1)
            .until(() -> !getErrorToasts(driver, message).isEmpty())
            .assertTrue("No error toast found with message " + message);
    }

    public static void verifySuccessToast(WebDriver driver, String message) {
        AwaitilityWrapper.create(10, 1)
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
}
