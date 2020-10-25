package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChPasswordPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChangePasswordParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ChangePasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.ConfirmPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_password.NewPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.account.AccountPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangePasswordTest extends SeleniumTest {
    @DataProvider(name = "invalidParameters", parallel = true)
    public Object[][] invalidParametersProvider() {
        return new Object[][]{
            new Object[]{ChangePasswordParameters.valid(), valid()},
            new Object[]{ChangePasswordParameters.tooShortPassword(), tooShortPassword()},
            new Object[]{ChangePasswordParameters.tooLongPassword(), tooLongPassword()},
            new Object[]{ChangePasswordParameters.invalidConfirmPassword(), invalidConfirmPassword()},
            new Object[]{ChangePasswordParameters.emptyPassword(), emptyPassword()},
        };
    }

    @Test(dataProvider = "invalidParameters")
    public void invalidParameters(ChangePasswordParameters parameters, ChangePasswordValidationResult validationResult) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillChangePasswordForm(driver, parameters);
        SleepUtil.sleep(2000);

        AccountPageActions.verifyChangePasswordForm(driver, validationResult);
    }

    @Test
    public void invalidPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangePasswordParameters parameters = ChangePasswordParameters.valid()
            .toBuilder()
            .password(DataConstants.INVALID_PASSWORD)
            .build();

        AccountPageActions.changePassword(driver, parameters);

        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
    }

    @Test
    public void successfulPasswordChange() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangePasswordParameters parameters = ChangePasswordParameters.valid();
        AccountPageActions.changePassword(driver, parameters);

        NotificationUtil.verifySuccessNotification(driver, "Jelszó megváltoztatva.");
        AccountPageActions.back(driver);

        ModulesPageActions.logout(driver);
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        NotificationUtil.verifyErrorNotification(driver, "Az email cím és jelszó kombinációja ismeretlen.");

        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(userData.getEmail()).password(parameters.getNewPassword()).build());
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

    private ChangePasswordValidationResult invalidConfirmPassword() {
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
