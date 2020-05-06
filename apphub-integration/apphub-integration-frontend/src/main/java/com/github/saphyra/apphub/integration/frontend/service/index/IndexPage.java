package com.github.saphyra.apphub.integration.frontend.service.index;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
class IndexPage {
    private static final By USER_NAME_INPUT = By.id("reg-username");
    private static final By PASSWORD_INPUT = By.id("reg-password");
    private static final By CONFIRM_PASSWORD_INPUT = By.id("reg-confirm-password");
    private static final By USER_NAME_VALID = By.id("invalid-username");
    private static final By PASSWORD_VALID = By.id("invalid-password");
    private static final By CONFIRM_PASSWORD_VALID = By.id("invalid-confirm-password");
    private static final By REGISTRATION_SUBMIT_BUTTON = By.id("registration-button");
    private static final By EMAIL_INPUT = By.id("reg-email");
    private static final By EMAIL_VALID = By.id("invalid-email");
    private static final By LOGIN_EMAIL = By.id("login-email");
    private static final By LOGIN_PASSWORD = By.id("login-password");
    private static final By LOGIN_BUTTON = By.id("login-button");

    public static WebElement emailInput(WebDriver driver) {
        return driver.findElement(EMAIL_INPUT);
    }

    public static WebElement emailValid(WebDriver driver) {
        return driver.findElement(EMAIL_VALID);
    }

    static WebElement usernameInput(WebDriver driver) {
        return driver.findElement(USER_NAME_INPUT);
    }

    static WebElement passwordInput(WebDriver driver) {
        return driver.findElement(PASSWORD_INPUT);
    }

    static WebElement confirmPasswordInput(WebDriver driver) {
        return driver.findElement(CONFIRM_PASSWORD_INPUT);
    }

    static WebElement usernameValid(WebDriver driver) {
        return driver.findElement(USER_NAME_VALID);
    }

    static WebElement passwordValid(WebDriver driver) {
        return driver.findElement(PASSWORD_VALID);
    }

    static WebElement confirmPasswordValid(WebDriver driver) {
        return driver.findElement(CONFIRM_PASSWORD_VALID);
    }

    static WebElement registrationSubmitButton(WebDriver driver) {
        return driver.findElement(REGISTRATION_SUBMIT_BUTTON);
    }

    public static WebElement loginEmail(WebDriver driver) {
        return driver.findElement(LOGIN_EMAIL);
    }

    public static WebElement loginPassword(WebDriver driver) {
        return driver.findElement(LOGIN_PASSWORD);
    }

    public static WebElement loginButton(WebDriver driver) {
        return driver.findElement(LOGIN_BUTTON);
    }
}
