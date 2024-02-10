package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.EmailValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ChangeEmailTest extends SeleniumTest {
    @Test(groups = {"fe", "account"})
    public void changeEmail() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters existingUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        blankEmail(driver);
        invalidEmail(driver);
        emptyPassword(driver);
        emailAlreadyExists(driver, existingUserData);
        incorrectPassword(driver);
        change(driver, userData);
    }

    private void blankEmail(WebDriver driver) {
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.blankEmail());
        AccountPageActions.verifyChangeEmailForm(driver, blankEmail());
    }

    private static void invalidEmail(WebDriver driver) {
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.invalidEmail());
        AccountPageActions.verifyChangeEmailForm(driver, invalidEmail());
    }

    private static void emptyPassword(WebDriver driver) {
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.emptyPassword());
        AccountPageActions.verifyChangeEmailForm(driver, emptyPassword());
    }

    private static void emailAlreadyExists(WebDriver driver, RegistrationParameters existingUserData) {
        ChangeEmailParameters emailAlreadyExistsParameters = ChangeEmailParameters.valid()
            .toBuilder()
            .email(existingUserData.getEmail())
            .build();

        AccountPageActions.fillChangeEmailForm(driver, emailAlreadyExistsParameters);
        AccountPageActions.verifyChangeEmailForm(driver, valid());
        AccountPageActions.changeEmail(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_EMAIL_ALREADY_IN_USE);
    }

    private static void incorrectPassword(WebDriver driver) {
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.incorrectPassword());
        AccountPageActions.verifyChangeEmailForm(driver, valid());
        AccountPageActions.changeEmail(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_INCORRECT_PASSWORD);
    }

    private static void change(WebDriver driver, RegistrationParameters userData) {
        ChangeEmailParameters changeParameters = ChangeEmailParameters.valid();
        AccountPageActions.fillChangeEmailForm(driver, changeParameters);
        AccountPageActions.verifyChangeEmailForm(driver, valid());
        AccountPageActions.changeEmail(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ACCOUNT_EMAIL_CHANGED);

        AccountPageActions.back(driver);
        ModulesPageActions.logout(driver);
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);
        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(changeParameters.getEmail()).password(userData.getPassword()).build());
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    private static ChangeEmailValidationResult valid() {
        return ChangeEmailValidationResult.builder()
            .email(EmailValidationResult.VALID)
            .password(ChEmailPasswordValidationResult.VALID)
            .build();
    }

    private ChangeEmailValidationResult blankEmail() {
        return valid()
            .toBuilder()
            .email(EmailValidationResult.BLANK)
            .build();
    }

    private static ChangeEmailValidationResult invalidEmail() {
        return valid()
            .toBuilder()
            .email(EmailValidationResult.INVALID)
            .build();
    }

    private static ChangeEmailValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChEmailPasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
