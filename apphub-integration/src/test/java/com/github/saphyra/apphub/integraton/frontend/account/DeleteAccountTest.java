package com.github.saphyra.apphub.integraton.frontend.account;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.delete_account.DeleteAccountPasswordValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends SeleniumTest {
    @Test(groups = {"fe", "account"})
    public void deleteAccount() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        //Empty password
        AccountPageActions.fillDeleteAccountForm(driver, "asd");
        AccountPageActions.fillDeleteAccountForm(driver, "");
        SleepUtil.sleep(3000);
        AccountPageActions.verifyDeleteAccountForm(driver, DeleteAccountPasswordValidationResult.EMPTY_PASSWORD);

        //Incorrect password
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                AccountPageActions.deleteAccount(driver, DataConstants.INCORRECT_PASSWORD);
                NotificationUtil.verifyErrorNotification(driver, "Incorrect password.");
            });

        AccountPageActions.deleteAccount(driver, DataConstants.INCORRECT_PASSWORD);
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.createWithRedirect(Endpoints.INDEX_PAGE, Endpoints.ACCOUNT_PAGE)))
            .assertTrue("User not logged out");
        ToastMessageUtil.verifyErrorToast(driver, "Account locked. Try again later.");

        //Cancel deletion
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ACCOUNT_PAGE))
            .assertTrue("Account page is not opened");

        AccountPageActions.fillDeleteAccountForm(driver, DataConstants.VALID_PASSWORD);
        AccountPageActions.submitDeleteAccountForm(driver);
        AccountPageActions.cancelAccountDeletion(driver);
        NotificationUtil.verifyZeroNotifications(driver);

        //Delete
        AccountPageActions.deleteAccount(driver, DataConstants.VALID_PASSWORD);
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.INDEX_PAGE)));
        //NotificationUtil.verifySuccessNotification(driver, "Account törölve."); TODO restore when account page is migrated to React
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE));
        ToastMessageUtil.verifyErrorToast(driver, "Unknown combination of e-mail address and password.");
    }
}
