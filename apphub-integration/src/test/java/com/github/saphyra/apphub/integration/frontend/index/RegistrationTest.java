package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.UsernameValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class RegistrationTest extends SeleniumTest {
    @Test(groups = {"fe", "index"})
    public void verifyValidation() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        invalidRegistrationParameters(driver);
        RegistrationParameters existingUser = registrationSuccessful(driver);
        usernameAlreadyExists(driver, existingUser);
        emailAlreadyExists(driver, existingUser);
    }

    private static void invalidRegistrationParameters(WebDriver driver) {
        executeValidationTest(driver, RegistrationParameters.tooShortUsernameParameters(), usernameTooShort());
        executeValidationTest(driver, RegistrationParameters.tooLongUsernameParameters(), usernameTooLong());
        executeValidationTest(driver, RegistrationParameters.tooShortPasswordParameters(), passwordTooShort());
        executeValidationTest(driver, RegistrationParameters.tooLongPasswordParameters(), passwordTooLong());
        executeValidationTest(driver, RegistrationParameters.incorrectConfirmPasswordParameters(), confirmPasswordIncorrect());
        executeValidationTest(driver, RegistrationParameters.invalidEmailParameters(), emailInvalid());
    }

    private static RegistrationParameters registrationSuccessful(WebDriver driver) {
        RegistrationParameters existingUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUser);
        ModulesPageActions.logout(driver);
        return existingUser;
    }

    private static void usernameAlreadyExists(WebDriver driver, RegistrationParameters existingUser) {
        RegistrationParameters usernameAlreadyExists = RegistrationParameters.validParameters().toBuilder()
            .username(existingUser.getUsername())
            .build();
        IndexPageActions.fillRegistrationForm(driver, usernameAlreadyExists);
        IndexPageActions.submitRegistration(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_USERNAME_ALREADY_IN_USE);
    }

    private static void emailAlreadyExists(WebDriver driver, RegistrationParameters existingUser) {
        RegistrationParameters emailAlreadyExists = RegistrationParameters.validParameters().toBuilder()
            .email(existingUser.getEmail())
            .build();
        IndexPageActions.fillRegistrationForm(driver, emailAlreadyExists);
        IndexPageActions.submitRegistration(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_EMAIL_ALREADY_IN_USE);
    }

    private static void executeValidationTest(WebDriver driver, RegistrationParameters parameters, RegistrationValidationResult validationResult) {
        IndexPageActions.fillRegistrationForm(driver, parameters);
        IndexPageActions.verifyRegistrationForm(driver, validationResult);
    }

    private static RegistrationValidationResult valid() {
        return RegistrationValidationResult.builder()
            .email(EmailValidationResult.VALID)
            .username(UsernameValidationResult.VALID)
            .password(PasswordValidationResult.VALID)
            .confirmPassword(PasswordValidationResult.VALID)
            .build();
    }

    private static RegistrationValidationResult usernameTooShort() {
        return valid().toBuilder()
            .username(UsernameValidationResult.TOO_SHORT)
            .build();
    }

    private static RegistrationValidationResult usernameTooLong() {
        return valid().toBuilder()
            .username(UsernameValidationResult.TOO_LONG)
            .build();
    }

    private static RegistrationValidationResult passwordTooShort() {
        return valid().toBuilder()
            .password(PasswordValidationResult.TOO_SHORT)
            .build();
    }

    private static RegistrationValidationResult passwordTooLong() {
        return valid().toBuilder()
            .password(PasswordValidationResult.TOO_LONG)
            .build();
    }

    private static RegistrationValidationResult confirmPasswordIncorrect() {
        return valid().toBuilder()
            .confirmPassword(PasswordValidationResult.INVALID_CONFIRM_PASSWORD)
            .build();
    }

    private static RegistrationValidationResult emailInvalid() {
        return valid().toBuilder()
            .email(EmailValidationResult.INVALID)
            .build();
    }
}
