package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.RolesForAllActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RolesForAllTest extends SeleniumTest {
    @Test(groups = {"fe", "admin-panel"})
    public void addAndRemoveRoleFromAll() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.ROLES_FOR_ALL);

        AwaitilityWrapper.createDefault()
            .until(() -> !RolesForAllActions.getRoles(driver).isEmpty())
            .assertTrue("Roles are not loaded.");

        restrictedRolesNotPresent(driver, userData);

        revokeFromAll_emptyPassword(driver);
        revokeFromAll_incorrectPassword(driver, userData);
        revokeFromAll(driver, userData);

        addToAll_emptyPassword(driver);
        addToAll_incorrectPassword(driver, userData);
        addToAll(driver, userData);

        revokeFromAll(driver, userData);
    }

    private void restrictedRolesNotPresent(WebDriver driver, RegistrationParameters registrationParameters) {
        RolesForAllActions.getRestrictedRoles(getServerPort(), registrationParameters)
            .forEach(role -> assertThat(RolesForAllActions.findRole(driver, role)).isEmpty());
    }

    private void addToAll(WebDriver driver, RegistrationParameters userData) {
        RolesForAllActions.findRoleValidated(driver, Constants.ROLE_TEST)
            .addToAll(driver);

        RolesForAllActions.fillPassword(driver, userData.getPassword());
        RolesForAllActions.confirmAddToAll(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ROLES_FOR_ALL_ROLE_ADDED);

        assertThat(DatabaseUtil.getRoleCount(Constants.ROLE_TEST)).isGreaterThan(0);
    }

    private void addToAll_incorrectPassword(WebDriver driver, RegistrationParameters userData) {
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                RolesForAllActions.fillPassword(driver, DataConstants.INCORRECT_PASSWORD);
                RolesForAllActions.confirmAddToAll(driver);
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
            });

        RolesForAllActions.fillPassword(driver, DataConstants.INCORRECT_PASSWORD);
        RolesForAllActions.confirmAddToAll(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        Integer serverPort = getServerPort();
        AwaitilityWrapper.create(20, 2)
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.createWithRedirect(serverPort, Endpoints.INDEX_PAGE, Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE)))
            .assertTrue("User is not logged out");

        IndexPageActions.submitLogin(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        SleepUtil.sleep(3000);

        IndexPageActions.submitLogin(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE))
            .assertTrue("User is not logged in");
    }

    private void addToAll_emptyPassword(WebDriver driver) {
        RolesForAllActions.findRoleValidated(driver, Constants.ROLE_TEST)
            .addToAll(driver);

        RolesForAllActions.fillPassword(driver, "");
        RolesForAllActions.confirmAddToAll(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.EMPTY_PASSWORD);
    }

    private void revokeFromAll(WebDriver driver, RegistrationParameters userData) {
        RolesForAllActions.findRoleValidated(driver, Constants.ROLE_TEST)
            .revokeFromAll(driver);

        RolesForAllActions.fillPassword(driver, userData.getPassword());
        RolesForAllActions.confirmRevokeFromAll(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.ROLES_FOR_ALL_ROLE_REVOKED);

        assertThat(DatabaseUtil.getRoleCount(Constants.ROLE_TEST)).isEqualTo(0);
    }

    private void revokeFromAll_emptyPassword(WebDriver driver) {
        RolesForAllActions.findRoleValidated(driver, Constants.ROLE_TEST)
            .revokeFromAll(driver);

        RolesForAllActions.fillPassword(driver, "");
        RolesForAllActions.confirmRevokeFromAll(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.EMPTY_PASSWORD);
    }

    private void revokeFromAll_incorrectPassword(WebDriver driver, RegistrationParameters userData) {
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                RolesForAllActions.fillPassword(driver, DataConstants.INCORRECT_PASSWORD);
                RolesForAllActions.confirmRevokeFromAll(driver);
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
            });

        RolesForAllActions.fillPassword(driver, DataConstants.INCORRECT_PASSWORD);
        RolesForAllActions.confirmRevokeFromAll(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        Integer serverPort = getServerPort();
        AwaitilityWrapper.create(20, 2)
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.createWithRedirect(serverPort, Endpoints.INDEX_PAGE, Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE)))
            .assertTrue("User is not logged out");

        IndexPageActions.submitLogin(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        SleepUtil.sleep(3000);

        IndexPageActions.submitLogin(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE))
            .assertTrue("User is not logged in");
    }
}
