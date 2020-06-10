package com.github.saphyra.apphub.integration.frontend.service.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class AccountPage {
    private static final By CHANGE_EMAIL_NEW_EMAIL_INPUT = By.id("ch-email-new-email-input");
    private static final By CHANGE_EMAIL_PASSWORD_INPUT = By.id("ch-email-password-input");
    private static final By CHANGE_EMAIL_INVALID_NEW_EMAIL = By.id("ch-email-invalid-new-email");
    private static final By CHANGE_EMAIL_INVALID_PASSWORD = By.id("ch-email-invalid-password");
    private static final By CHANGE_EMAIL_SUBMIT_BUTTON = By.id("change-email-button");
    private static final By BACK_BUTTON = By.id("back-button");

    public static WebElement changeEmailNewEmailInput(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_NEW_EMAIL_INPUT);
    }

    public static WebElement changeEmailPasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_PASSWORD_INPUT);
    }

    public static WebElement changeEmailInvalidNewEmail(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_INVALID_NEW_EMAIL);
    }

    public static WebElement changeEmailInvalidPassword(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_INVALID_PASSWORD);
    }

    public static WebElement changeEmailSubmitButton(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_SUBMIT_BUTTON);
    }

    public static WebElement backButton(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }
}
