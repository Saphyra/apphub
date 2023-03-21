package com.github.saphyra.apphub.integration.action.frontend.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

class AccountPage {
    private static final By CHANGE_EMAIL_NEW_EMAIL_INPUT = By.id("ch-email-new-email-input");
    private static final By CHANGE_EMAIL_PASSWORD_INPUT = By.id("ch-email-password-input");
    private static final By CHANGE_EMAIL_INVALID_NEW_EMAIL = By.id("ch-email-invalid-new-email");
    private static final By CHANGE_EMAIL_INVALID_PASSWORD = By.id("ch-email-invalid-password");
    private static final By CHANGE_EMAIL_SUBMIT_BUTTON = By.id("change-email-button");
    private static final By BACK_BUTTON = By.id("back-button");
    private static final By CHANGE_USERNAME_NEW_USERNAME_INPUT = By.id("ch-username-new-username-input");
    private static final By CHANGE_USERNAME_PASSWORD_INPUT = By.id("ch-username-password-input");
    private static final By CHANGE_USERNAME_INVALID_NEW_USERNAME = By.id("ch-username-invalid-new-username");
    private static final By CHANGE_USERNAME_INVALID_PASSWORD = By.id("ch-username-invalid-password");
    private static final By CHANGE_USERNAME_SUBMIT_BUTTON = By.id("change-username-button");
    private static final By CHANGE_PASSWORD_NEW_PASSWORD_INPUT = By.id("ch-password-new-password-input");
    private static final By CHANGE_PASSWORD_CONFIRM_PASSWORD_INPUT = By.id("ch-password-confirm-password-input");
    private static final By CHANGE_PASSWORD_PASSWORD_INPUT = By.id("ch-password-password-input");
    private static final By CHANGE_PASSWORD_INVALID_NEW_PASSWORD = By.id("ch-password-invalid-new-password");
    private static final By CHANGE_PASSWORD_INVALID_CONFIRM_PASSWORD = By.id("ch-password-invalid-confirm-password");
    private static final By CHANGE_PASSWORD_INVALID_PASSWORD = By.id("ch-password-invalid-password");
    private static final By CHANGE_PASSWORD_SUBMIT_BUTTON = By.id("change-password-button");
    private static final By DELETE_ACCOUNT_PASSWORD_INPUT = By.id("delete-account-password-input");
    private static final By DELETE_ACCOUNT_INVALID_PASSWORD = By.id("delete-account-invalid-password");
    private static final By DELETE_ACCOUNT_SUBMIT_BUTTON = By.id("delete-account-button");
    private static final By CHANGE_LANGUAGE_INPUT = By.id("ch-language-input");
    private static final By LANGUAGE_OPTIONS = By.cssSelector("#ch-language-input option");
    private static final By CHANGE_LANGUAGE_BUTTON = By.id("change-language-button");
    private static final By DELETE_ACCOUNT_CONFIRMATION_DIALOG_CONFIRM_BUTTON = By.cssSelector("#account-deletion-confirmation-dialog .confirmation-dialog-confirm-button");
    private static final By DELETE_ACCOUNT_CONFIRMATION_DIALOG_DECLINE_BUTTON = By.cssSelector("#account-deletion-confirmation-dialog .confirmation-dialog-decline-button");

    public static WebElement changeEmailNewEmailInput(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_NEW_EMAIL_INPUT);
    }

    public static WebElement changeEmailPasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_PASSWORD_INPUT);
    }

    public static Optional<WebElement> changeEmailInvalidNewEmail(WebDriver driver) {
        return driver.findElements(CHANGE_EMAIL_INVALID_NEW_EMAIL)
            .stream()
            .findFirst();
    }

    public static Optional<WebElement> changeEmailInvalidPassword(WebDriver driver) {
        return driver.findElements(CHANGE_EMAIL_INVALID_PASSWORD)
            .stream()
            .findFirst();
    }

    public static WebElement changeEmailSubmitButton(WebDriver driver) {
        return driver.findElement(CHANGE_EMAIL_SUBMIT_BUTTON);
    }

    public static WebElement backButton(WebDriver driver) {
        return driver.findElement(BACK_BUTTON);
    }

    public static WebElement changeUsernameNewUsernameInput(WebDriver driver) {
        return driver.findElement(CHANGE_USERNAME_NEW_USERNAME_INPUT);
    }

    public static WebElement changeUsernamePasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_USERNAME_PASSWORD_INPUT);
    }

    public static Optional<WebElement> changeUsernameInvalidNewUsername(WebDriver driver) {
        return driver.findElements(CHANGE_USERNAME_INVALID_NEW_USERNAME)
            .stream()
            .findFirst();
    }

    public static Optional<WebElement> changeUsernameInvalidPassword(WebDriver driver) {
        return driver.findElements(CHANGE_USERNAME_INVALID_PASSWORD)
            .stream()
            .findFirst();
    }

    public static WebElement changeUsernameSubmitButton(WebDriver driver) {
        return driver.findElement(CHANGE_USERNAME_SUBMIT_BUTTON);
    }

    public static WebElement changePasswordNewPasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_PASSWORD_NEW_PASSWORD_INPUT);
    }

    public static WebElement changePasswordConfirmPasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_PASSWORD_CONFIRM_PASSWORD_INPUT);
    }

    public static WebElement changePasswordPasswordInput(WebDriver driver) {
        return driver.findElement(CHANGE_PASSWORD_PASSWORD_INPUT);
    }

    public static Optional<WebElement> changePasswordInvalidNewPassword(WebDriver driver) {
        return driver.findElements(CHANGE_PASSWORD_INVALID_NEW_PASSWORD)
            .stream()
            .findFirst();
    }

    public static Optional<WebElement> changePasswordInvalidConfirmPassword(WebDriver driver) {
        return driver.findElements(CHANGE_PASSWORD_INVALID_CONFIRM_PASSWORD)
            .stream()
            .findFirst();
    }

    public static Optional<WebElement> changePasswordInvalidPassword(WebDriver driver) {
        return driver.findElements(CHANGE_PASSWORD_INVALID_PASSWORD)
            .stream()
            .findFirst();
    }

    public static WebElement changePasswordSubmitButton(WebDriver driver) {
        return driver.findElement(CHANGE_PASSWORD_SUBMIT_BUTTON);
    }

    public static WebElement deleteAccountPasswordInput(WebDriver driver) {
        return driver.findElement(DELETE_ACCOUNT_PASSWORD_INPUT);
    }

    public static Optional<WebElement> deleteAccountInvalidPassword(WebDriver driver) {
        return driver.findElements(DELETE_ACCOUNT_INVALID_PASSWORD)
            .stream()
            .findFirst();
    }

    public static WebElement deleteAccountSubmitButton(WebDriver driver) {
        return driver.findElement(DELETE_ACCOUNT_SUBMIT_BUTTON);
    }

    public static WebElement changeLanguageInput(WebDriver driver) {
        return driver.findElement(CHANGE_LANGUAGE_INPUT);
    }

    public static List<WebElement> languageOptions(WebDriver driver) {
        return driver.findElements(LANGUAGE_OPTIONS);
    }

    public static WebElement changeLanguageButton(WebDriver driver) {
        return driver.findElement(CHANGE_LANGUAGE_BUTTON);
    }

    public static WebElement deleteAccountConfirmationDialogConfirmButton(WebDriver driver) {
        return driver.findElement(DELETE_ACCOUNT_CONFIRMATION_DIALOG_CONFIRM_BUTTON);
    }

    public static WebElement deleteAccountConfirmationDialogDeclineButton(WebDriver driver) {
        return driver.findElement(DELETE_ACCOUNT_CONFIRMATION_DIALOG_DECLINE_BUTTON);
    }
}
