package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.account.AccountPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangeEmailTest extends SeleniumTest {
    @DataProvider(name = "invalidParameters")
    public Object[][] invalidParametersProvider() {
        return new Object[][]{
            new Object[]{ChangeEmailParameters.valid(), valid()},
            new Object[]{ChangeEmailParameters.invalidEmail(), invalidEmail()},
            new Object[]{ChangeEmailParameters.emptyPassword(), emptyPassword()}
        };
    }

    @Test(dataProvider = "invalidParameters")
    public void invalidParameters(ChangeEmailParameters parameters, ChangeEmailValidationResult validationResult) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillChangeEmailForm(driver, parameters);
        SleepUtil.sleep(2000);

        AccountPageActions.verifyChangeEmailForm(driver, validationResult);
    }

    @Test
    public void emailAlreadyExists() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters existingUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeEmailParameters parameters = ChangeEmailParameters.valid()
            .toBuilder()
            .email(existingUserData.getEmail())
            .build();
        AccountPageActions.changeEmail(driver, parameters);

        NotificationUtil.verifyErrorNotification(driver, "Az email foglalt.");
    }

    @Test
    public void incorrectPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeEmailParameters parameters = ChangeEmailParameters.valid()
            .toBuilder()
            .password(DataConstants.INVALID_PASSWORD)
            .build();
        AccountPageActions.changeEmail(driver, parameters);

        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
    }

    @Test
    public void successfulEmailChange() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeEmailParameters parameters = ChangeEmailParameters.valid();
        AccountPageActions.changeEmail(driver, parameters);

        NotificationUtil.verifySuccessNotification(driver, "Email cím megváltoztatva.");
        AccountPageActions.back(driver);

        ModulesPageActions.logout(driver);
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        NotificationUtil.verifyErrorNotification(driver, "Az email cím és jelszó kombinációja ismeretlen.");

        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(parameters.getEmail()).password(userData.getPassword()).build());
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    private ChangeEmailValidationResult valid() {
        return ChangeEmailValidationResult.builder()
            .email(EmailValidationResult.VALID)
            .password(ChEmailPasswordValidationResult.VALID)
            .build();
    }

    private ChangeEmailValidationResult invalidEmail() {
        return valid()
            .toBuilder()
            .email(EmailValidationResult.INVALID)
            .build();
    }

    private ChangeEmailValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChEmailPasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
