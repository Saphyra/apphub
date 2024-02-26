package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.EmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChPasswordPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ConfirmPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.NewPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.UsernameValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.delete_account.DeleteAccountPasswordValidationResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountPageActions {
    public static void changeEmail(WebDriver driver) {
        changeEmailButton(driver)
            .click();
    }

    public static void fillChangeEmailForm(WebDriver driver, ChangeEmailParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-email-email-input")), parameters.getEmail());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-email-password-input")), parameters.getPassword());
    }

    public static void verifyChangeEmailForm(WebDriver driver, ChangeEmailValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-email-email-input-validation"),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-email-password-input-validation"),
            validationResult.getPassword() != ChEmailPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changeEmailButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changeEmailButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-email-button"));
    }

    public static void back(WebDriver driver) {
        driver.findElement(By.id("account-home-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    public static void changeUsername(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> changeUsernameButton(driver).isEnabled())
            .assertTrue();
        changeUsernameButton(driver).click();
    }

    public static void fillChangeUsernameForm(WebDriver driver, ChangeUsernameParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-username-username-input")), parameters.getUsername());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-username-password-input")), parameters.getPassword());
    }

    public static void verifyChangeUsernameForm(WebDriver driver, ChangeUsernameValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-username-username-input-validation"),
            validationResult.getUsername() != UsernameValidationResult.VALID,
            validationResult.getUsername().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-username-password-input-validation"),
            validationResult.getPassword() != ChUsernamePasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changeUsernameButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changeUsernameButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-username-button"));
    }

    public static void changePassword(WebDriver driver) {
        changePasswordButton(driver)
            .click();
    }

    public static void fillChangePasswordForm(WebDriver driver, ChangePasswordParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-password-new-password-input")), parameters.getNewPassword());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-password-confirm-password-input")), parameters.getConfirmPassword());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-password-current-password-input")), parameters.getCurrentPassword());
    }

    public static void verifyChangePasswordForm(WebDriver driver, ChangePasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-new-password-input-validation"),
            validationResult.getNewPassword() != NewPasswordValidationResult.VALID,
            validationResult.getNewPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-confirm-password-input-validation"),
            validationResult.getConfirmPassword() != ConfirmPasswordValidationResult.VALID,
            validationResult.getConfirmPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-current-password-input-validation"),
            validationResult.getPassword() != ChPasswordPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changePasswordButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changePasswordButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-password-button"));
    }

    public static void deleteAccount(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> deleteAccountButton(driver).isEnabled())
            .assertTrue();
        deleteAccountButton(driver)
            .click();

        driver.findElement(By.id("account-delete-account-confirm-button"))
            .click();
    }

    public static void fillDeleteAccountForm(WebDriver driver, String password) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFillContentEditable(driver, By.id("account-delete-account-password-input"), password);
    }

    public static void verifyDeleteAccountForm(WebDriver driver, DeleteAccountPasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-delete-account-password-input-validation"),
            validationResult != DeleteAccountPasswordValidationResult.VALID,
            validationResult.getErrorMessage()
        );

        assertThat(deleteAccountButton(driver).isEnabled()).isEqualTo(validationResult == DeleteAccountPasswordValidationResult.VALID);
    }

    private static WebElement deleteAccountButton(WebDriver driver) {
        return driver.findElement(By.id("account-delete-account-button"));
    }

    public static void selectLanguage(WebDriver driver, Language language) {
        driver.findElement(By.cssSelector("#language-selector ." + language.getLocale()))
            .click();
    }

    public static Language getActiveLanguage(WebDriver driver) {
        return getLanguageSelectorButtons(driver)
            .stream()
            .map(WebElementUtils::getClasses)
            .filter(classes -> classes.contains("current"))
            .findFirst()
            .map(Language::fromClasses)
            .orElseThrow(() -> new RuntimeException("Active language not found."));
    }

    private static List<WebElement> getLanguageSelectorButtons(WebDriver driver) {
        return driver.findElements(By.cssSelector("#language-selector .language"));
    }

    public static void toggleDeactivateAllSessions(WebDriver driver) {
        driver.findElement(By.id("account-change-password-deactivate-all-sessions"))
            .click();
    }

    public static String getCurrentEmail(WebDriver driver) {
        return driver.findElement(By.id("account-current-email"))
            .getText();
    }

    public static String getCurrentUsername(WebDriver driver) {
        return driver.findElement(By.id("account-current-username"))
            .getText();
    }
}
