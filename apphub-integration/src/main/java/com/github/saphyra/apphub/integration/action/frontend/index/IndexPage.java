package com.github.saphyra.apphub.integration.action.frontend.index;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
class IndexPage {
    static WebElement emailInput(WebDriver driver) {
        return AwaitilityWrapper.getWithWait(() -> {
                try {
                    return driver.findElement(By.id("registration-email"));
                } catch (Exception e) {
                    return null;
                }
            },
            t -> !isNull(t)
        ).orElseThrow(() -> new RuntimeException("Email input not present on url " + driver.getCurrentUrl()));
    }

    static Optional<WebElement> emailValid(WebDriver driver) {
        return driver.findElements(By.id("registration-email-validation"))
            .stream()
            .findFirst();
    }

    static WebElement usernameInput(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("registration-username")));
    }

    static WebElement passwordInput(WebDriver driver) {
        return driver.findElement(By.id("registration-password"));
    }

    static WebElement confirmPasswordInput(WebDriver driver) {
        return driver.findElement(By.id("registration-confirm-password"));
    }

    static Optional<WebElement> usernameValid(WebDriver driver) {
        return driver.findElements(By.id("registration-username-validation"))
            .stream()
            .findFirst();
    }

    static Optional<WebElement> passwordValid(WebDriver driver) {
        return driver.findElements(By.id("registration-password-validation"))
            .stream()
            .findFirst();
    }

    static Optional<WebElement> confirmPasswordValid(WebDriver driver) {
        return driver.findElements(By.id("registration-confirm-password-validation"))
            .stream()
            .findFirst();
    }

    static WebElement registrationSubmitButton(WebDriver driver) {
        return driver.findElement(By.id("registration-button"));
    }

    static WebElement loginUserIdentifier(WebDriver driver) {
        return driver.findElement(By.id("login-user-identifier"));
    }

    static WebElement loginPassword(WebDriver driver) {
        return driver.findElement(By.id("login-password"));
    }

    static WebElement loginButton(WebDriver driver) {
        return driver.findElement(By.id("login-button"));
    }
}
