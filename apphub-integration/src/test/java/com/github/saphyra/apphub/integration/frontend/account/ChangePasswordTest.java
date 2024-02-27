package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.account.ChangePasswordActions;
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
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChPasswordPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ConfirmPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.NewPasswordValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

public class ChangePasswordTest extends SeleniumTest {
    @Test(groups = {"fe", "account"})
    public void changePassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        tooShortPassword(driver);
        tooLongPassword(driver);
        incorrectConfirmPassword(driver);
        emptyPassword(driver);
        incorrectPassword(driver);
        change(driver, userData);
    }

    private static void tooShortPassword(WebDriver driver) {
        ChangePasswordActions.fillChangePasswordForm(driver, ChangePasswordParameters.tooShortPassword());
        ChangePasswordActions.verifyChangePasswordForm(driver, tooShortPassword());
    }

    private static void tooLongPassword(WebDriver driver) {
        ChangePasswordActions.fillChangePasswordForm(driver, ChangePasswordParameters.tooLongPassword());
        ChangePasswordActions.verifyChangePasswordForm(driver, tooLongPassword());
    }

    private static void incorrectConfirmPassword(WebDriver driver) {
        ChangePasswordActions.fillChangePasswordForm(driver, ChangePasswordParameters.incorrectConfirmPassword());
        ChangePasswordActions.verifyChangePasswordForm(driver, incorrectConfirmPassword());
    }

    private static void emptyPassword(WebDriver driver) {
        ChangePasswordActions.fillChangePasswordForm(driver, ChangePasswordParameters.emptyPassword());
        ChangePasswordActions.verifyChangePasswordForm(driver, emptyPassword());
    }

    private static void incorrectPassword(WebDriver driver) {
        ChangePasswordActions.fillChangePasswordForm(driver, ChangePasswordParameters.incorrectCurrentPassword());
        ChangePasswordActions.verifyChangePasswordForm(driver, valid());
        ChangePasswordActions.changePassword(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_INCORRECT_PASSWORD);
    }

    private static void change(WebDriver driver, RegistrationParameters userData) {
        ChangePasswordParameters changeParameters = ChangePasswordParameters.valid();

        ChangePasswordActions.fillChangePasswordForm(driver, changeParameters);
        ChangePasswordActions.verifyChangePasswordForm(driver, valid());
        ChangePasswordActions.changePassword(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ACCOUNT_PASSWORD_CHANGED);

        AccountPageActions.back(driver);
        ModulesPageActions.logout(driver);

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);

        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(userData.getEmail()).password(changeParameters.getNewPassword()).build());
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    private static ChangePasswordValidationResult valid() {
        return ChangePasswordValidationResult.builder()
            .newPassword(NewPasswordValidationResult.VALID)
            .confirmPassword(ConfirmPasswordValidationResult.VALID)
            .password(ChPasswordPasswordValidationResult.VALID)
            .build();
    }

    private static ChangePasswordValidationResult tooShortPassword() {
        return valid()
            .toBuilder()
            .newPassword(NewPasswordValidationResult.TOO_SHORT)
            .build();
    }

    private static ChangePasswordValidationResult tooLongPassword() {
        return valid()
            .toBuilder()
            .newPassword(NewPasswordValidationResult.TOO_LONG)
            .build();
    }

    private static ChangePasswordValidationResult incorrectConfirmPassword() {
        return valid()
            .toBuilder()
            .confirmPassword(ConfirmPasswordValidationResult.INVALID_CONFIRM_PASSWORD)
            .build();
    }

    private static ChangePasswordValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChPasswordPasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }

    @Test(groups = {"fe", "account"})
    public void changePassword_deactivateAllSessions() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        Navigation.toIndexPage(driver1);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver1, userData);
        ModulesPageActions.openModule(driver1, ModuleLocation.MANAGE_ACCOUNT);

        Navigation.toIndexPage(driver2);
        IndexPageActions.submitLogin(driver2, LoginParameters.fromRegistrationParameters(userData));

        ChangePasswordParameters changeParameters = ChangePasswordParameters.valid();
        ChangePasswordActions.fillChangePasswordForm(driver1, changeParameters);
        ChangePasswordActions.toggleDeactivateAllSessions(driver1);
        ChangePasswordActions.changePassword(driver1);

        AwaitilityWrapper.create(20, 3)
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.INDEX_PAGE))
            .assertTrue("Secondary session is not invalidated.");
        ToastMessageUtil.verifySuccessToast(driver1, LocalizedText.ACCOUNT_PASSWORD_CHANGED);

        AwaitilityWrapper.create(20, 3)
            .until(() -> driver2.getCurrentUrl().equals(UrlFactory.create(Endpoints.INDEX_PAGE + "?redirect=" + Endpoints.MODULES_PAGE)))
            .assertTrue("Secondary session is not invalidated. PageUrl: " + driver2.getCurrentUrl());
    }

}
