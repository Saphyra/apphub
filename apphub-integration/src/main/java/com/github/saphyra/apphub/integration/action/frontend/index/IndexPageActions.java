package com.github.saphyra.apphub.integration.action.frontend.index;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.registration.UsernameValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.Callable;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class IndexPageActions {
    public static void fillRegistrationForm(WebDriver driver, RegistrationParameters parameters) {
        SleepUtil.sleep(1000);

        log.debug("Filling registrationForm with {}", parameters);
        clearAndFill(IndexPage.emailInput(driver), parameters.getEmail());
        clearAndFill(IndexPage.usernameInput(driver), parameters.getUsername());
        clearAndFill(IndexPage.passwordInput(driver), parameters.getPassword());
        clearAndFill(IndexPage.confirmPasswordInput(driver), parameters.getConfirmPassword());
    }

    public static void verifyRegistrationForm(WebDriver driver, RegistrationValidationResult validationResult) {
        assertThat(driver.getCurrentUrl()).endsWith(Endpoints.INDEX_PAGE);

        verifyInvalidFieldState(
            IndexPage.emailValid(driver),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyInvalidFieldState(
            IndexPage.usernameValid(driver),
            validationResult.getUsername() != UsernameValidationResult.VALID,
            validationResult.getUsername().getErrorMessage()
        );

        verifyInvalidFieldState(
            IndexPage.passwordValid(driver),
            validationResult.getPassword() != PasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            IndexPage.confirmPasswordValid(driver),
            validationResult.getConfirmPassword() != PasswordValidationResult.VALID,
            validationResult.getConfirmPassword().getErrorMessage()
        );

        assertThat(IndexPage.registrationSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    public static void registerUser(WebDriver driver, RegistrationParameters registrationParameters) {
        registerUser(driver, registrationParameters, () -> driver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE));
    }

    public static void registerUser(WebDriver driver, RegistrationParameters registrationParameters, Callable<Boolean> verification) {
        fillRegistrationForm(driver, registrationParameters);
        submitRegistration(driver);

        AwaitilityWrapper.createDefault()
            .until(verification)
            .assertTrue("Registration failed.");
    }

    public static void submitRegistration(WebDriver driver) {
        WebElement submitButton = IndexPage.registrationSubmitButton(driver);

        AwaitilityWrapper.createDefault()
            .until(submitButton::isEnabled)
            .assertTrue("Registration form is not valid.");
        submitButton.click();
    }

    public static void submitLogin(int serverPort, WebDriver driver, LoginParameters loginParameters) {
        AwaitilityWrapper.createDefault()
            .until(() -> isLoginPageLoaded(serverPort, driver))
            .assertTrue("LoginPage is not loaded.");

        clearAndFill(IndexPage.loginUserIdentifier(driver), loginParameters.getUserIdentifier());
        clearAndFill(IndexPage.loginPassword(driver), loginParameters.getPassword());

        IndexPage.loginButton(driver).click();
    }

    public static boolean isLoginPageLoaded(int serverPort, WebDriver driver) {
        return driver.getCurrentUrl().split("\\?")[0].equals(UrlFactory.create(serverPort, Endpoints.INDEX_PAGE));
    }
}
