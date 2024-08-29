package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChPasswordPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ChangePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.ConfirmPasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_password.NewPasswordValidationResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordActions {
    public static void changePassword(WebDriver driver) {
        changePasswordButton(driver)
            .click();
    }

    public static void fillChangePasswordForm(int serverPort, WebDriver driver, ChangePasswordParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, Endpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-password-new-password-input")), parameters.getNewPassword());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-password-confirm-password-input")), parameters.getConfirmPassword());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-password-current-password-input")), parameters.getCurrentPassword());
    }

    public static void verifyChangePasswordForm(WebDriver driver, ChangePasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-new-password-input-validation"),
            validationResult.getNewPassword() != NewPasswordValidationResult.VALID,
            validationResult.getNewPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-confirm-password-input-validation"),
            validationResult.getConfirmPassword() != ConfirmPasswordValidationResult.VALID,
            validationResult.getConfirmPassword().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-password-current-password-input-validation"),
            validationResult.getPassword() != ChPasswordPasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changePasswordButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changePasswordButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-password-button"));
    }

    public static void toggleDeactivateAllSessions(WebDriver driver) {
        driver.findElement(By.id("account-change-password-deactivate-all-sessions"))
            .click();
    }
}
