package com.github.saphyra.apphub.integraton.frontend.account;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
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

public class ChangePasswordTest extends SeleniumTest {
    @Test(groups = {"fe", "account"})
    public void changePassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        //Too short password
        AccountPageActions.fillChangePasswordForm(driver, ChangePasswordParameters.tooShortPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangePasswordForm(driver, tooShortPassword());

        //Too long password
        AccountPageActions.fillChangePasswordForm(driver, ChangePasswordParameters.tooLongPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangePasswordForm(driver, tooLongPassword());

        //Incorrect confirm password
        AccountPageActions.fillChangePasswordForm(driver, ChangePasswordParameters.incorrectConfirmPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangePasswordForm(driver, incorrectConfirmPassword());

        //Empty password
        AccountPageActions.fillChangePasswordForm(driver, ChangePasswordParameters.emptyPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangePasswordForm(driver, emptyPassword());

        //Incorrect password
        ChangePasswordParameters incorrectPasswordParameters = ChangePasswordParameters.valid()
            .toBuilder()
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        AccountPageActions.changePassword(driver, incorrectPasswordParameters);
        NotificationUtil.verifyErrorNotification(driver, "Incorrect password.");

        //Change
        ChangePasswordParameters changeParameters = ChangePasswordParameters.valid();
        AccountPageActions.changePassword(driver, changeParameters);
        NotificationUtil.verifySuccessNotification(driver, "Password Changed.");
        AccountPageActions.back(driver);
        ModulesPageActions.logout(driver);
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, "Unknown combination of e-mail address and password.");
        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(userData.getEmail()).password(changeParameters.getNewPassword()).build());
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    private ChangePasswordValidationResult valid() {
        return ChangePasswordValidationResult.builder()
            .newPassword(NewPasswordValidationResult.VALID)
            .confirmPassword(ConfirmPasswordValidationResult.VALID)
            .password(ChPasswordPasswordValidationResult.VALID)
            .build();
    }

    private ChangePasswordValidationResult tooShortPassword() {
        return valid()
            .toBuilder()
            .newPassword(NewPasswordValidationResult.TOO_SHORT)
            .build();
    }

    private ChangePasswordValidationResult tooLongPassword() {
        return valid()
            .toBuilder()
            .newPassword(NewPasswordValidationResult.TOO_LONG)
            .build();
    }

    private ChangePasswordValidationResult incorrectConfirmPassword() {
        return valid()
            .toBuilder()
            .confirmPassword(ConfirmPasswordValidationResult.INVALID_CONFIRM_PASSWORD)
            .build();
    }

    private ChangePasswordValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChPasswordPasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
