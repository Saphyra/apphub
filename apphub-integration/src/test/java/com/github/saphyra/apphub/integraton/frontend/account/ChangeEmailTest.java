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
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.user.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.structure.user.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.structure.user.change_email.EmailValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ChangeEmailTest extends SeleniumTest {
    @Test
    public void changeEmail() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters existingUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        //Invalid email
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.invalidEmail());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangeEmailForm(driver, invalidEmail());

        //Empty password
        AccountPageActions.fillChangeEmailForm(driver, ChangeEmailParameters.emptyPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangeEmailForm(driver, emptyPassword());

        //Email already exists
        ChangeEmailParameters emailAlreadyExistsParameters = ChangeEmailParameters.valid()
            .toBuilder()
            .email(existingUserData.getEmail())
            .build();
        AccountPageActions.changeEmail(driver, emailAlreadyExistsParameters);
        NotificationUtil.verifyErrorNotification(driver, "Az email foglalt.");

        //Incorrect password
        ChangeEmailParameters incorrectPasswordParameters = ChangeEmailParameters.valid()
            .toBuilder()
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        AccountPageActions.changeEmail(driver, incorrectPasswordParameters);
        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");

        //Change
        ChangeEmailParameters changeParameters = ChangeEmailParameters.valid();
        AccountPageActions.changeEmail(driver, changeParameters);
        NotificationUtil.verifySuccessNotification(driver, "Email cím megváltoztatva.");
        AccountPageActions.back(driver);
        ModulesPageActions.logout(driver);
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, "Az email cím és jelszó kombinációja ismeretlen.");
        IndexPageActions.submitLogin(driver, LoginParameters.builder().email(changeParameters.getEmail()).password(userData.getPassword()).build());
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
