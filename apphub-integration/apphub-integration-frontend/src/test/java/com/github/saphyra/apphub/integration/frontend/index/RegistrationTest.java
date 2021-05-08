package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends SeleniumTest {
    @DataProvider(name = "registrationParameters", parallel = true)
    public Object[][] registrationParameters() {
        return new Object[][]{
            {RegistrationParameters.validParameters(), valid()},
            {RegistrationParameters.tooShortUsernameParameters(), usernameTooShort()},
            {RegistrationParameters.tooLongUsernameParameters(), usernameTooLong()},
            {RegistrationParameters.tooShortPasswordParameters(), passwordTooShort()},
            {RegistrationParameters.tooLongPasswordParameters(), passwordTooLong()},
            {RegistrationParameters.incorrectConfirmPasswordParameters(), confirmPasswordIncorrect()},
            {RegistrationParameters.invalidEmailParameters(), emailInvalid()}
        };
    }

    @Test(dataProvider = "registrationParameters")
    public void verifyValidation(RegistrationParameters parameters, RegistrationValidationResult validationResult) {
        //GIVEN
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        //WHEN
        IndexPageActions.fillRegistrationForm(driver, parameters);
        SleepUtil.sleep(2000);
        //THEN
        IndexPageActions.verifyRegistrationForm(driver, validationResult);
    }

    @Test
    public void emailAlreadyExists() {
        //GIVEN
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters existingUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUser);
        ModulesPageActions.logout(driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters().toBuilder()
            .email(existingUser.getEmail())
            .build();
        IndexPageActions.fillRegistrationForm(driver, registrationParameters);
        //WHEN
        IndexPageActions.submitRegistration(driver);
        //THEN
        NotificationUtil.verifyErrorNotification(driver, "Az email foglalt.");

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE));
    }

    @Test
    public void usernameAlreadyExists() {
        //GIVEN
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters existingUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUser);
        ModulesPageActions.logout(driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters().toBuilder()
            .username(existingUser.getUsername())
            .build();
        IndexPageActions.fillRegistrationForm(driver, registrationParameters);
        //WHEN
        IndexPageActions.submitRegistration(driver);
        //THEN
        NotificationUtil.verifyErrorNotification(driver, "A felhasználónév foglalt.");

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE));
    }

    @Test
    public void successfulRegistration() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters existingUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUser);

        NotificationUtil.verifySuccessNotification(driver, "Sikeres regisztráció.");
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
