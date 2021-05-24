package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.UsernameValidationResult;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
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
        NotificationUtil.verifySuccessNotification(driver, "Sikeres regisztráció.");
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
        //WHEN
        IndexPageActions.submitRegistration(driver);
        //THEN
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
