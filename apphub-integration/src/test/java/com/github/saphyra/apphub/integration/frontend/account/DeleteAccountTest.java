package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.account.DeleteAccountActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
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
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MANAGE_ACCOUNT);

        emptyPassword(driver);
        incorrectPassword(driver);
        delete(driver, userData);
    }

    private static void emptyPassword(WebDriver driver) {
        DeleteAccountActions.fillDeleteAccountForm(getServerPort(), driver, "asd");
        DeleteAccountActions.fillDeleteAccountForm(getServerPort(), driver, "");
        DeleteAccountActions.verifyDeleteAccountForm(driver, DeleteAccountPasswordValidationResult.EMPTY_PASSWORD);
    }

    private static void incorrectPassword(WebDriver driver) {
        int serverPort = getServerPort();
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DeleteAccountActions.fillDeleteAccountForm(serverPort, driver, DataConstants.INCORRECT_PASSWORD);
                DeleteAccountActions.deleteAccount(driver);
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
            });

        DeleteAccountActions.fillDeleteAccountForm(serverPort, driver, DataConstants.INCORRECT_PASSWORD);
        DeleteAccountActions.deleteAccount(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        AwaitilityWrapper.create(20, 3)
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.createWithRedirect(serverPort, GenericEndpoints.INDEX_PAGE, UserEndpoints.ACCOUNT_PAGE)))
            .assertTrue("User not logged out");

    }

    private static void delete(WebDriver driver, RegistrationParameters userData) {
        int serverPort = getServerPort();

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.login(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_PAGE));
        });

        DeleteAccountActions.fillDeleteAccountForm(serverPort, driver, DataConstants.VALID_PASSWORD);
        DeleteAccountActions.deleteAccount(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(serverPort, GenericEndpoints.INDEX_PAGE)));
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ACCOUNT_DELETED);

        IndexPageActions.login(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, GenericEndpoints.INDEX_PAGE));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);
    }
}
