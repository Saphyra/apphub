package com.github.saphyra.apphub.integration.frontend.service.account;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.ChangeEmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.EmailValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_email.PasswordValidationResult;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.verifyInvalidFieldState;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountPageActions {
    public static void changeEmail(WebDriver driver, ChangeEmailParameters parameters) {
        fillChangeEmailForm(driver, parameters);
        AwaitilityWrapper.createDefault()
            .until(() -> AccountPage.changeEmailSubmitButton(driver).isEnabled())
            .assertTrue();
        AccountPage.changeEmailSubmitButton(driver).click();
    }

    public static void fillChangeEmailForm(WebDriver driver, ChangeEmailParameters parameters) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));

        clearAndFill(AccountPage.changeEmailNewEmailInput(driver), parameters.getEmail());
        clearAndFill(AccountPage.changeEmailPasswordInput(driver), parameters.getPassword());
    }

    public static void verifyChangeEmailForm(WebDriver driver, ChangeEmailValidationResult validationResult) {
        NotificationUtil.verifyZeroNotifications(driver);

        verifyInvalidFieldState(
            AccountPage.changeEmailInvalidNewEmail(driver),
            validationResult.getEmail() != EmailValidationResult.VALID,
            validationResult.getEmail().getErrorMessage()
        );

        verifyInvalidFieldState(
            AccountPage.changeEmailInvalidPassword(driver),
            validationResult.getPassword() != PasswordValidationResult.VALID,
            validationResult.getPassword().getErrorMessage()
        );

        assertThat(AccountPage.changeEmailSubmitButton(driver).isEnabled()).isEqualTo(validationResult.allValid());
    }

    public static void back(WebDriver driver) {
        AccountPage.backButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }
}
