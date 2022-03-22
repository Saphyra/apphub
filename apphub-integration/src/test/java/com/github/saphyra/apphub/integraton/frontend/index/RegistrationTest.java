package com.github.saphyra.apphub.integraton.frontend.index;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.UsernameValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class RegistrationTest extends SeleniumTest {
    @Test
    public void verifyValidation() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        //Invalid registration parameters
        executeValidationTest(driver, RegistrationParameters.tooShortUsernameParameters(), usernameTooShort());
        executeValidationTest(driver, RegistrationParameters.tooLongUsernameParameters(), usernameTooLong());
        executeValidationTest(driver, RegistrationParameters.tooShortPasswordParameters(), passwordTooShort());
        executeValidationTest(driver, RegistrationParameters.tooLongPasswordParameters(), passwordTooLong());
        executeValidationTest(driver, RegistrationParameters.incorrectConfirmPasswordParameters(), confirmPasswordIncorrect());
        executeValidationTest(driver, RegistrationParameters.invalidEmailParameters(), emailInvalid());

        //Registration successful
        RegistrationParameters existingUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUser);
        ModulesPageActions.logout(driver);

        //Username already exists
        RegistrationParameters usernameAlreadyExists = RegistrationParameters.validParameters().toBuilder()
            .username(existingUser.getUsername())
            .build();
        IndexPageActions.fillRegistrationForm(driver, usernameAlreadyExists);
        IndexPageActions.submitRegistration(driver);

        NotificationUtil.verifyErrorNotification(driver, "A felhasználónév foglalt.");

        //Email already exists
        RegistrationParameters emailAlreadyExists = RegistrationParameters.validParameters().toBuilder()
            .email(existingUser.getEmail())
            .build();
        IndexPageActions.fillRegistrationForm(driver, emailAlreadyExists);
        IndexPageActions.submitRegistration(driver);
        NotificationUtil.verifyErrorNotification(driver, "Az email foglalt.");
    }

    private void executeValidationTest(WebDriver driver, RegistrationParameters parameters, RegistrationValidationResult validationResult) {
        IndexPageActions.fillRegistrationForm(driver, parameters);
        SleepUtil.sleep(2000);
        IndexPageActions.verifyRegistrationForm(driver, validationResult);
    }

    private RegistrationValidationResult valid() {
        return RegistrationValidationResult.builder()
            .email(EmailValidationResult.VALID)
            .username(UsernameValidationResult.VALID)
            .password(PasswordValidationResult.VALID)
            .confirmPassword(PasswordValidationResult.VALID)
            .build();
    }

    private RegistrationValidationResult usernameTooShort() {
        return valid().toBuilder()
            .username(UsernameValidationResult.TOO_SHORT)
            .build();
    }

    private RegistrationValidationResult usernameTooLong() {
        return valid().toBuilder()
            .username(UsernameValidationResult.TOO_LONG)
            .build();
    }

    private RegistrationValidationResult passwordTooShort() {
        return valid().toBuilder()
            .password(PasswordValidationResult.TOO_SHORT)
            .build();
    }

    private RegistrationValidationResult passwordTooLong() {
        return valid().toBuilder()
            .password(PasswordValidationResult.TOO_LONG)
            .build();
    }

    private RegistrationValidationResult confirmPasswordIncorrect() {
        return valid().toBuilder()
            .confirmPassword(PasswordValidationResult.INVALID_CONFIRM_PASSWORD)
            .build();
    }

    private RegistrationValidationResult emailInvalid() {
        return valid().toBuilder()
            .email(EmailValidationResult.INVALID)
            .build();
    }
}
