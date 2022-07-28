package com.github.saphyra.apphub.integration.action.frontend.index;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.structure.user.registration.UsernameValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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
        fillRegistrationForm(driver, registrationParameters);
        submitRegistration(driver);

        ConditionFactory conditionFactory = await()
            .atMost(15, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS);
        AwaitilityWrapper.wrap(conditionFactory)
            .until(() -> WebElementUtils.getIfPresent(() -> IndexPage.registrationSubmitButton(driver)).isEmpty())
            .assertTrue("Registration failed. Notifications: " + NotificationUtil.getNotificationTexts(driver));
    }

    public static void submitRegistration(WebDriver driver) {
        WebElement submitButton = IndexPage.registrationSubmitButton(driver);

        AwaitilityWrapper.createDefault()
            .until(submitButton::isEnabled)
            .assertTrue("Registration form is not valid.");
        submitButton.click();
    }

    public static void submitLogin(WebDriver driver, LoginParameters loginParameters) {
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().split("\\?")[0].equals(UrlFactory.create(Endpoints.INDEX_PAGE)))
            .assertTrue("LoginPage is not loaded.");

        clearAndFill(IndexPage.loginEmail(driver), loginParameters.getEmail());
        clearAndFill(IndexPage.loginPassword(driver), loginParameters.getPassword());

        IndexPage.loginButton(driver).click();
    }
}
