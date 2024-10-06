package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChEmailPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_email.EmailValidationResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeEmailActions {
    public static void changeEmail(WebDriver driver) {
        changeEmailButton(driver)
            .click();
    }

    public static void fillChangeEmailForm(int serverPort, WebDriver driver, ChangeEmailParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-email-email-input")), parameters.getEmail());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-email-password-input")), parameters.getPassword());
    }

    public static void verifyChangeEmailForm(WebDriver driver, ChangeEmailValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-email-email-input-validation"),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-email-password-input-validation"),
            validationResult.getPassword() != ChEmailPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changeEmailButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changeEmailButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-email-button"));
    }

    public static String getCurrentEmail(WebDriver driver) {
        return driver.findElement(By.id("account-current-email"))
            .getText();
    }
}
