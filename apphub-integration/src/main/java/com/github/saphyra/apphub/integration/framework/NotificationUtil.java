package com.github.saphyra.apphub.integration.framework;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Deprecated
public class NotificationUtil {
    private static final By NOTIFICATIONS_LOCATOR = By.cssSelector("#notification-container > DIV");
    private static final By NOTIFICATION_TEXT_LOCATOR = By.cssSelector(":first-child");

    public static void verifyErrorNotification(WebDriver driver, String notificationMessage) {
        waitUntilNotificationVisible(driver, notificationMessage);
        Optional<WebElement> matchingNotification = getMatchingNotification(driver, notificationMessage);
        WebElement element = matchingNotification.get()
            .findElement(NOTIFICATION_TEXT_LOCATOR);
        String backgroundColor = element.getCssValue("backgroundColor");
        if (!backgroundColor.equals("rgba(255, 0, 0, 1)")) {
            throw new AssertionError("Notification's background color is not red. It is " + backgroundColor);
        }

        clearNotifications(driver);
    }

    public static void verifySuccessNotification(WebDriver driver, String notificationMessage) {
        waitUntilNotificationVisible(driver, notificationMessage);
        Optional<WebElement> matchingNotification = getMatchingNotification(driver, notificationMessage);
        WebElement element = matchingNotification.get()
            .findElement(NOTIFICATION_TEXT_LOCATOR);
        String backgroundColor = element.getCssValue("backgroundColor");
        if (!backgroundColor.equals("rgba(0, 128, 0, 1)")) {
            throw new AssertionError("Notification's background color is not green. It is " + backgroundColor);
        }

        clearNotifications(driver);
    }

    public static void verifyZeroNotifications(WebDriver driver) {
        assertThat(getNotifications(driver)).isEmpty();
    }

    private static void waitUntilNotificationVisible(WebDriver driver, String notificationMessage) {
        AwaitilityWrapper.createDefault()
            .until(() -> getNotifications(driver).stream()
                .anyMatch(webElement -> webElement.findElement(NOTIFICATION_TEXT_LOCATOR).getText().equals(notificationMessage)));
    }

    private static Optional<WebElement> getMatchingNotification(WebDriver driver, String notificationMessage) {
        Optional<WebElement> matchingNotification = getNotifications(driver).stream()
            .peek(webElement -> log.debug("Notification found with message {}", webElement.findElement(NOTIFICATION_TEXT_LOCATOR).getText()))
            .filter(webElement -> webElement.findElement(NOTIFICATION_TEXT_LOCATOR).getText().equals(notificationMessage))
            .findAny();
        if (matchingNotification.isEmpty()) {
            throw new AssertionError("No notification matches notificationMessage " + notificationMessage);
        }
        return matchingNotification;
    }

    public static void clearNotifications(WebDriver driver) {
        try {
            getNotifications(driver)
                .stream()
                .filter(WebElement::isDisplayed)
                .forEach(NotificationUtil::click);
        } catch (Exception e) {
            log.error("Failed clearing notifications", e);
        }
    }

    private static void click(WebElement webElement) {
        try {
            webElement.click();
        } catch (Exception e) {
            log.warn("Element {} not clickable: {}", webElement, e.getMessage());
        }
    }

    private static List<WebElement> getNotifications(WebDriver driver) {
        return driver.findElements(NOTIFICATIONS_LOCATOR);
    }

    public static List<String> getNotificationTexts(WebDriver driver) {
        return getNotifications(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
