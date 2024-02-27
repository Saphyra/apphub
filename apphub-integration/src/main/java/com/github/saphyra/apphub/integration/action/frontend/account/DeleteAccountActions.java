package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.user.delete_account.DeleteAccountPasswordValidationResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountActions {
    public static void deleteAccount(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> deleteAccountButton(driver).isEnabled())
            .assertTrue();
        deleteAccountButton(driver)
            .click();

        driver.findElement(By.id("account-delete-account-confirm-button"))
            .click();
    }

    public static void fillDeleteAccountForm(WebDriver driver, String password) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFillContentEditable(driver, By.id("account-delete-account-password-input"), password);
    }

    public static void verifyDeleteAccountForm(WebDriver driver, DeleteAccountPasswordValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-delete-account-password-input-validation"),
            validationResult != DeleteAccountPasswordValidationResult.VALID,
            validationResult.getErrorMessage()
        );

        assertThat(deleteAccountButton(driver).isEnabled()).isEqualTo(validationResult == DeleteAccountPasswordValidationResult.VALID);
    }

    private static WebElement deleteAccountButton(WebDriver driver) {
        return driver.findElement(By.id("account-delete-account-button"));
    }
}
