package com.github.saphyra.apphub.integration.frontend.service.index;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.model.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.UsernameValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
public class IndexPageActions {
    public static void fillRegistrationForm(WebDriver driver, RegistrationParameters parameters) {
        assertThat(driver.getCurrentUrl()).endsWith(Endpoints.WEB_ROOT);

        log.info("Filling registrationForm with {}", parameters);
        clearAndFill(IndexPage.emailInput(driver), parameters.getEmail());
        clearAndFill(IndexPage.usernameInput(driver), parameters.getUsername());
        clearAndFill(IndexPage.passwordInput(driver), parameters.getPassword());
        clearAndFill(IndexPage.confirmPasswordInput(driver), parameters.getConfirmPassword());
    }

    public static void verifyRegistrationForm(WebDriver driver, RegistrationValidationResult validationResult) {
        assertThat(driver.getCurrentUrl()).endsWith(Endpoints.WEB_ROOT);

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
        fillRegistrationForm(driver, registrationParameters);
        submitRegistration(driver);

        ConditionFactory conditionFactory = await()
            .atMost(15, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS);
        AwaitilityWrapper.wrap(conditionFactory)
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue("Registration failed.");
    }

    public static void submitRegistration(WebDriver driver) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.WEB_ROOT));
        WebElement submitButton = IndexPage.registrationSubmitButton(driver);

        AwaitilityWrapper.createDefault()
            .until(submitButton::isEnabled)
            .assertTrue("Registration form is not valid.");
        submitButton.click();
    }

    public static void submitLogin(WebDriver driver, LoginParameters loginParameters) {
        clearAndFill(IndexPage.loginEmail(driver), loginParameters.getEmail());
        clearAndFill(IndexPage.loginPassword(driver), loginParameters.getPassword());

        IndexPage.loginButton(driver).click();
    }
}
