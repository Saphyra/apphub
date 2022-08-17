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
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.delete_account.DeleteAccountPasswordValidationResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends SeleniumTest {
    @Test
    public void deleteAccount() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        //Empty password
        AccountPageActions.fillDeleteAccountForm(driver, "");
        SleepUtil.sleep(2000);
        AccountPageActions.verifyDeleteAccountForm(driver, DeleteAccountPasswordValidationResult.EMPTY_PASSWORD);

        //Incorrect password
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                AccountPageActions.deleteAccount(driver, DataConstants.INCORRECT_PASSWORD);
                NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
            });

        AccountPageActions.deleteAccount(driver, DataConstants.INCORRECT_PASSWORD);
        NotificationUtil.verifyErrorNotification(driver, "Fiók zárolva. Próbáld újra később!");

        //Cancel deletion
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillDeleteAccountForm(driver, DataConstants.VALID_PASSWORD);
        AccountPageActions.submitDeleteAccountForm(driver);
        AccountPageActions.cancelAccountDeletion(driver);
        NotificationUtil.verifyZeroNotifications(driver);

        //Delete
        AccountPageActions.deleteAccount(driver, DataConstants.VALID_PASSWORD);
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.INDEX_PAGE)));
        NotificationUtil.verifySuccessNotification(driver, "Account törölve.");
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE));
        NotificationUtil.verifyErrorNotification(driver, "Az email cím és jelszó kombinációja ismeretlen.");
    }
}
