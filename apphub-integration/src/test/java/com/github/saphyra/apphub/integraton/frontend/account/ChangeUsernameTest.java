package com.github.saphyra.apphub.integraton.frontend.account;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.UsernameValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ChangeUsernameTest extends SeleniumTest {
    @Test(groups = {"fe", "account"})
    public void changeUsername() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters existingUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        tooShortUsername(driver);
        tooLongUsername(driver);
        emptyPassword(driver);
        usernameAlreadyExists(driver, existingUserData);
        incorrectPassword(driver);
        change(driver);
    }

    private void tooShortUsername(WebDriver driver) {
        AccountPageActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.tooShortUsername());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangeUsernameForm(driver, tooShortUsername());
    }

    private void tooLongUsername(WebDriver driver) {
        AccountPageActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.tooLongUsername());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangeUsernameForm(driver, tooLongUsername());
    }

    private void emptyPassword(WebDriver driver) {
        AccountPageActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.emptyPassword());
        SleepUtil.sleep(2000);
        AccountPageActions.verifyChangeUsernameForm(driver, emptyPassword());
    }

    private static void usernameAlreadyExists(WebDriver driver, RegistrationParameters existingUserData) {
        ChangeUsernameParameters usernameAlreadyExistsParameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .username(existingUserData.getUsername())
            .build();
        AccountPageActions.changeUsername(driver, usernameAlreadyExistsParameters);
        NotificationUtil.verifyErrorNotification(driver, "Username already in use.");
    }

    private static void incorrectPassword(WebDriver driver) {
        ChangeUsernameParameters incorrectPasswordParameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        AccountPageActions.changeUsername(driver, incorrectPasswordParameters);
        NotificationUtil.verifyErrorNotification(driver, "Incorrect password.");
    }

    private static void change(WebDriver driver) {
        ChangeUsernameParameters changeParameters = ChangeUsernameParameters.valid();
        AccountPageActions.changeUsername(driver, changeParameters);
        NotificationUtil.verifySuccessNotification(driver, "Username changed successfully.");
    }

    private ChangeUsernameValidationResult valid() {
        return ChangeUsernameValidationResult.builder()
            .username(UsernameValidationResult.VALID)
            .password(ChUsernamePasswordValidationResult.VALID)
            .build();
    }

    private ChangeUsernameValidationResult tooShortUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_SHORT)
            .build();
    }

    private ChangeUsernameValidationResult tooLongUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_LONG)
            .build();
    }

    private ChangeUsernameValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChUsernamePasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
