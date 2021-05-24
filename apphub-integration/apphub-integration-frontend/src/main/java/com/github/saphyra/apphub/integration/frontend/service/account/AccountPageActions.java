package com.github.saphyra.apphub.integration.frontend.service.account;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChPasswordPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChangePasswordParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChangePasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ConfirmPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.NewPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.UsernameValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.delete_account.DeleteAccountPasswordValidationResult;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountPageActions {
    public static void changeEmail(WebDriver driver, ChangeEmailParameters parameters) {
        fillChangeEmailForm(driver, parameters);
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.changeEmailSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.changeEmailSubmitButton(driver).click();
    }

    public static void fillChangeEmailForm(WebDriver driver, ChangeEmailParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(AccountPage.changeEmailNewEmailInput(driver), parameters.getEmail());
        clearAndFill(AccountPage.changeEmailPasswordInput(driver), parameters.getPassword());
    }

    public static void verifyChangeEmailForm(WebDriver driver, ChangeEmailValidationResult validationResult) {
        verifyInvalidFieldState(
            AccountPage.changeEmailInvalidNewEmail(driver),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyInvalidFieldState(
            AccountPage.changeEmailInvalidPassword(driver),
            validationResult.getPassword() != ChEmailPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(AccountPage.changeEmailSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    public static void back(WebDriver driver) {
        AccountPage.backButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    public static void changeUsername(WebDriver driver, ChangeUsernameParameters parameters) {
        fillChangeUsernameForm(driver, parameters);
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.changeUsernameSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.changeUsernameSubmitButton(driver).click();
    }

    public static void fillChangeUsernameForm(WebDriver driver, ChangeUsernameParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(AccountPage.changeUsernameNewUsernameInput(driver), parameters.getUsername());
        clearAndFill(AccountPage.changeUsernamePasswordInput(driver), parameters.getPassword());
    }

    public static void verifyChangeUsernameForm(WebDriver driver, ChangeUsernameValidationResult validationResult) {
        verifyInvalidFieldState(
            AccountPage.changeUsernameInvalidNewUsername(driver),
            validationResult.getUsername() != UsernameValidationResult.VALID,
            validationResult.getUsername().getErrorMessage()
        );

        verifyInvalidFieldState(
            AccountPage.changeUsernameInvalidPassword(driver),
            validationResult.getPassword() != ChUsernamePasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(AccountPage.changeUsernameSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    public static void changePassword(WebDriver driver, ChangePasswordParameters parameters) {
        fillChangePasswordForm(driver, parameters);
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.changePasswordSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.changePasswordSubmitButton(driver).click();
    }

    public static void fillChangePasswordForm(WebDriver driver, ChangePasswordParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(AccountPage.changePasswordNewPasswordInput(driver), parameters.getNewPassword());
        clearAndFill(AccountPage.changePasswordConfirmPasswordInput(driver), parameters.getConfirmPassword());
        clearAndFill(AccountPage.changePasswordPasswordInput(driver), parameters.getPassword());
    }

    public static void verifyChangePasswordForm(WebDriver driver, ChangePasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            AccountPage.changePasswordInvalidNewPassword(driver),
            validationResult.getNewPassword() != NewPasswordValidationResult.VALID,
            validationResult.getNewPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            AccountPage.changePasswordInvalidConfirmPassword(driver),
            validationResult.getConfirmPassword() != ConfirmPasswordValidationResult.VALID,
            validationResult.getConfirmPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            AccountPage.changePasswordInvalidPassword(driver),
            validationResult.getPassword() != ChPasswordPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(AccountPage.changePasswordSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    public static void deleteAccount(WebDriver driver, String password) {
        fillDeleteAccountForm(driver, password);
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.deleteAccountSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.deleteAccountSubmitButton(driver).click();

        AccountPage.deleteAccountConfirmationDialogConfirmButton(driver).click();
    }

    public static void fillDeleteAccountForm(WebDriver driver, String password) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(AccountPage.deleteAccountPasswordInput(driver), password);
    }

    public static void submitDeleteAccountForm(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.deleteAccountSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.deleteAccountSubmitButton(driver).click();
    }

    public static void verifyDeleteAccountForm(WebDriver driver, DeleteAccountPasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            AccountPage.deleteAccountInvalidPassword(driver),
            validationResult != DeleteAccountPasswordValidationResult.VALID,
            validationResult.getErrorMessage()
        );

        assertThat(AccountPage.deleteAccountSubmitButton(driver).isEnabled()).isEqualTo(validationResult == DeleteAccountPasswordValidationResult.VALID);
    }

    public static void selectLanguage(WebDriver driver, Language language) {
        AccountPage.changeLanguageInput(driver).click();
        AccountPage.languageOptions(driver)
            .stream()
            .filter(element -> element.getAttribute("value").equals(language.getLocale()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Option not found for language " + language))
            .click();
        AccountPage.changeLanguageButton(driver).click();
    }

    public static void verifyLanguageSelected(WebDriver driver, Language language) {
        assertThat(AccountPage.changeLanguageInput(driver).getAttribute("value")).isEqualTo(language.getLocale());
    }

    public static void cancelAccountDeletion(WebDriver driver) {
        AccountPage.deleteAccountConfirmationDialogDeclineButton(driver).click();
    }
}
