package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.structure.api.user.change_username.UsernameValidationResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUsernameActions {
    public static void changeUsername(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> changeUsernameButton(driver).isEnabled())
            .assertTrue();
        changeUsernameButton(driver).click();
    }

    public static void fillChangeUsernameForm(WebDriver driver, ChangeUsernameParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(driver.findElement(By.id("account-change-username-username-input")), parameters.getUsername());
        clearAndFillContentEditable(driver, driver.findElement(By.id("account-change-username-password-input")), parameters.getPassword());
    }

    public static void verifyChangeUsernameForm(WebDriver driver, ChangeUsernameValidationResult validationResult) {
        verifyInvalidFieldState(
            driver,
            By.id("account-change-username-username-input-validation"),
            validationResult.getUsername() != UsernameValidationResult.VALID,
            validationResult.getUsername().getErrorMessage()
        );

        verifyInvalidFieldState(
            driver,
            By.id("account-change-username-password-input-validation"),
            validationResult.getPassword() != ChUsernamePasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(changeUsernameButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    private static WebElement changeUsernameButton(WebDriver driver) {
        return driver.findElement(By.id("account-change-username-button"));
    }

    public static String getCurrentUsername(WebDriver driver) {
        return driver.findElement(By.id("account-current-username"))
            .getText();
    }
}
