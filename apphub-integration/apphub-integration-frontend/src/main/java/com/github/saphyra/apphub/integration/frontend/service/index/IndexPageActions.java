package com.github.saphyra.apphub.integration.frontend.service.index;

import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.registration.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.PasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.RegistrationValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.registration.UsernameValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class IndexPageActions {
    public static void fillRegistrationForm(WebDriver driver, RegistrationParameters parameters) {
        assertThat(driver.getCurrentUrl()).endsWith(Endpoint.WEB_ROOT);

        log.info("Filling registrationForm with {}", parameters);
        clearAndFill(IndexPage.emailInput(driver), parameters.getEmail());
        clearAndFill(IndexPage.usernameInput(driver), parameters.getUsername());
        clearAndFill(IndexPage.passwordInput(driver), parameters.getPassword());
        clearAndFill(IndexPage.confirmPasswordInput(driver), parameters.getConfirmPassword());
    }

    public static void verifyRegistrationForm(WebDriver driver, RegistrationValidationResult validationResult) {
        assertThat(driver.getCurrentUrl()).endsWith(Endpoint.WEB_ROOT);

        verifyState(
            IndexPage.emailValid(driver),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyState(
            IndexPage.usernameValid(driver),
            validationResult.getUsername() != UsernameValidationResult.VALID,
            validationResult.getUsername().getErrorMessage()
        );

        verifyState(
            IndexPage.passwordValid(driver),
            validationResult.getPassword() != PasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        verifyState(
            IndexPage.confirmPasswordValid(driver),
            validationResult.getConfirmPassword() != PasswordValidationResult.VALID,
            validationResult.getConfirmPassword().getErrorMessage()
        );

        assertThat(IndexPage.registrationSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static void verifyState(WebElement inputValid, boolean shouldBeVisible, String errorMessage) {
        if (shouldBeVisible) {
            assertThat(inputValid.isDisplayed()).isTrue();
            assertThat(inputValid.getAttribute("title")).isEqualTo(errorMessage);
        } else {
            assertThat(inputValid.isDisplayed()).isFalse();
        }
    }

    public static void registerUser(WebDriver driver, RegistrationParameters registrationParameters) {
        fillRegistrationForm(driver, registrationParameters);
        submitRegistration(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoint.MODULES_PAGE)));
    }

    public static void submitRegistration(WebDriver driver) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoint.WEB_ROOT));
        WebElement submitButton = IndexPage.registrationSubmitButton(driver);

        AwaitilityWrapper.createDefault()
            .until(submitButton::isEnabled);
        submitButton.click();
    }
}
