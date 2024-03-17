package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.account.ChangeUsernameActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.UsernameValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        ChangeUsernameActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.tooShortUsername());
        ChangeUsernameActions.verifyChangeUsernameForm(driver, tooShortUsername());
    }

    private void tooLongUsername(WebDriver driver) {
        ChangeUsernameActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.tooLongUsername());
        ChangeUsernameActions.verifyChangeUsernameForm(driver, tooLongUsername());
    }

    private void emptyPassword(WebDriver driver) {
        ChangeUsernameActions.fillChangeUsernameForm(driver, ChangeUsernameParameters.emptyPassword());
        ChangeUsernameActions.verifyChangeUsernameForm(driver, emptyPassword());
    }

    private static void usernameAlreadyExists(WebDriver driver, RegistrationParameters existingUserData) {
        ChangeUsernameParameters usernameAlreadyExistsParameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .username(existingUserData.getUsername())
            .build();

        ChangeUsernameActions.fillChangeUsernameForm(driver, usernameAlreadyExistsParameters);
        ChangeUsernameActions.verifyChangeUsernameForm(driver, valid());
        ChangeUsernameActions.changeUsername(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_USERNAME_ALREADY_IN_USE);
    }

    private static void incorrectPassword(WebDriver driver) {
        ChangeUsernameParameters incorrectPasswordParameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();

        ChangeUsernameActions.fillChangeUsernameForm(driver, incorrectPasswordParameters);
        ChangeUsernameActions.verifyChangeUsernameForm(driver, valid());
        ChangeUsernameActions.changeUsername(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
    }

    private static void change(WebDriver driver) {
        ChangeUsernameParameters parameters = ChangeUsernameParameters.valid();
        ChangeUsernameActions.fillChangeUsernameForm(driver, parameters);
        ChangeUsernameActions.verifyChangeUsernameForm(driver, valid());
        ChangeUsernameActions.changeUsername(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ACCOUNT_USERNAME_CHANGED);
        assertThat(ChangeUsernameActions.getCurrentUsername(driver)).isEqualTo(parameters.getUsername());
    }

    private static ChangeUsernameValidationResult valid() {
        return ChangeUsernameValidationResult.builder()
            .username(UsernameValidationResult.VALID)
            .password(ChUsernamePasswordValidationResult.VALID)
            .build();
    }

    private static ChangeUsernameValidationResult tooShortUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_SHORT)
            .build();
    }

    private static ChangeUsernameValidationResult tooLongUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_LONG)
            .build();
    }

    private static ChangeUsernameValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChUsernamePasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
