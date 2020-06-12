package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.account.delete_account.DeleteAccountPasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.account.AccountPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends SeleniumTest {
    @Test
    public void emptyPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillDeleteAccountForm(driver, "");
        SleepUtil.sleep(2000);

        AccountPageActions.verifyDeleteAccountForm(driver, DeleteAccountPasswordValidationResult.EMPTY_PASSWORD);
    }

    @Test
    public void incorrectPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.deleteAccount(driver, DataConstants.INVALID_PASSWORD);

        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
    }

    @Test
    public void cancelDeletion(){
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillDeleteAccountForm(driver, DataConstants.VALID_PASSWORD);
        AccountPageActions.submitDeleteAccountForm(driver);
        AccountPageActions.cancelAccountDeletion(driver);

        NotificationUtil.verifyZeroNotifications(driver);

        AccountPageActions.back(driver);
        ModulesPageActions.logout(driver);

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
    }
    
    @Test
    public void successfulDeletion() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.deleteAccount(driver, DataConstants.VALID_PASSWORD);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.WEB_ROOT)));

        NotificationUtil.verifySuccessNotification(driver, "Account törölve.");

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.WEB_ROOT));
        NotificationUtil.verifyErrorNotification(driver, "Az email cím és jelszó kombinációja ismeretlen.");
    }
}
