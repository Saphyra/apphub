package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
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
import com.github.saphyra.apphub.integration.structure.view.admin_panel.RoleManagementSearchResultUser;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class RoleManagementTest extends SeleniumTest {
    @Test(groups = {"fe", "admin-panel"})
    public void addAndRemoveRole() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver adminDriver = drivers.get(0);
        WebDriver testUserDriver = drivers.get(1);

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        RegistrationParameters testUserData = RegistrationParameters.validParameters();

        int serverPort = getServerPort();

        Stream.of(new BiWrapper<>(adminDriver, adminUserData), new BiWrapper<>(testUserDriver, testUserData))
            .parallel()
            .map(biWrapper -> EXECUTOR_SERVICE.execute(() -> {
                WebDriver driver = biWrapper.getEntity1();
                RegistrationParameters userData = biWrapper.getEntity2();

                Navigation.toIndexPage(serverPort, driver);
                IndexPageActions.registerUser(driver, userData);
            }))
            .forEach(future -> future.get(30, TimeUnit.SECONDS));

        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);

        SleepUtil.sleep(3000);
        adminDriver.navigate().refresh();
        ModulesPageActions.openModule(serverPort, adminDriver, ModuleLocation.ROLE_MANAGEMENT);

        addRole_emptyPassword(adminDriver, testUserData);
        addRole_incorrectPassword(adminDriver, adminUserData);
        addRole(adminDriver, testUserDriver, adminUserData, testUserData);

        revokeRole_emptyPassword(adminDriver, testUserData);
        revokeRole_incorrectPassword(adminDriver, adminUserData);
        revokeRole(adminDriver, testUserDriver, adminUserData, testUserData);
    }

    private void revokeRole(WebDriver adminDriver, WebDriver testUserDriver, RegistrationParameters adminUserData, RegistrationParameters testUserData) {
        RoleManagementActions.search(adminDriver, testUserData.getEmail());

        RoleManagementSearchResultUser testUser = AwaitilityWrapper.getOptionalWithWait(() -> RoleManagementActions.findUserByEmail(adminDriver, testUserData.getEmail()))
            .orElseThrow(() -> new RuntimeException("TestUser not found as search result."));

        testUser.getGrantedRoles()
            .stream()
            .filter(role -> role.getRole().equals(Constants.ROLE_ADMIN))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Admin role is not available."))
            .revoke(adminDriver);

        RoleManagementActions.fillPassword(adminDriver, adminUserData.getPassword());
        RoleManagementActions.confirmRevokeRole(adminDriver);

        ToastMessageUtil.verifySuccessToast(adminDriver, LocalizedText.ROLE_MANAGEMENT_ROLE_REVOKED);

        SleepUtil.sleep(3000);
        testUserDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> !ModulesPageActions.getCategories(testUserDriver).isEmpty())
            .assertTrue("Modules are not loaded.");
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(testUserDriver).stream().noneMatch(category -> category.getCategoryId().equals(ModuleLocation.ROLE_MANAGEMENT.getCategoryId())))
            .assertTrue("Admin role is not granted.");
    }

    private void revokeRole_incorrectPassword(WebDriver adminDriver, RegistrationParameters adminUserData) {
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                RoleManagementActions.fillPassword(adminDriver, DataConstants.INCORRECT_PASSWORD);
                RoleManagementActions.confirmRevokeRole(adminDriver);
                ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.INCORRECT_PASSWORD);
            });

        RoleManagementActions.fillPassword(adminDriver, DataConstants.INCORRECT_PASSWORD);
        RoleManagementActions.confirmRevokeRole(adminDriver);
        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.ACCOUNT_LOCKED);

        Integer serverPort = getServerPort();
        AwaitilityWrapper.create(20, 2)
            .until(() -> adminDriver.getCurrentUrl().equals(UrlFactory.createWithRedirect(serverPort, Endpoints.INDEX_PAGE, Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE)))
            .assertTrue("User is not logged out");

        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        SleepUtil.sleep(3000);

        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> adminDriver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE))
            .assertTrue("User is not logged in");
    }

    private void revokeRole_emptyPassword(WebDriver adminDriver, RegistrationParameters testUserData) {
        RoleManagementSearchResultUser testUser = AwaitilityWrapper.getOptionalWithWait(() -> RoleManagementActions.findUserByEmail(adminDriver, testUserData.getEmail()))
            .orElseThrow(() -> new RuntimeException("TestUser not found as search result."));

        testUser.getGrantedRoles()
            .get(0)
            .revoke(adminDriver);

        RoleManagementActions.confirmRevokeRole(adminDriver);

        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.EMPTY_PASSWORD);
    }

    private void addRole(WebDriver adminDriver, WebDriver testUserDriver, RegistrationParameters adminUserData, RegistrationParameters testUserData) {
        RoleManagementActions.search(adminDriver, testUserData.getEmail());

        RoleManagementSearchResultUser testUser = AwaitilityWrapper.getOptionalWithWait(() -> RoleManagementActions.findUserByEmail(adminDriver, testUserData.getEmail()))
            .orElseThrow(() -> new RuntimeException("TestUser not found as search result."));

        testUser.getAvailableRoles()
            .stream()
            .filter(role -> role.getRole().equals(Constants.ROLE_ADMIN))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Admin role is not available."))
            .grant(adminDriver);

        RoleManagementActions.fillPassword(adminDriver, adminUserData.getPassword());
        RoleManagementActions.confirmGrantRole(adminDriver);

        ToastMessageUtil.verifySuccessToast(adminDriver, LocalizedText.ROLE_MANAGEMENT_ROLE_GRANTED);

        SleepUtil.sleep(3000);
        testUserDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(testUserDriver).stream().anyMatch(category -> category.getCategoryId().equals(ModuleLocation.ROLE_MANAGEMENT.getCategoryId())))
            .assertTrue("Admin role is not granted.");
    }

    private void addRole_incorrectPassword(WebDriver adminDriver, RegistrationParameters adminUserData) {
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                RoleManagementActions.fillPassword(adminDriver, DataConstants.INCORRECT_PASSWORD);
                RoleManagementActions.confirmGrantRole(adminDriver);
                ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.INCORRECT_PASSWORD);
            });

        RoleManagementActions.fillPassword(adminDriver, DataConstants.INCORRECT_PASSWORD);
        RoleManagementActions.confirmGrantRole(adminDriver);
        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.ACCOUNT_LOCKED);

        Integer serverPort = getServerPort();
        AwaitilityWrapper.create(20, 2)
            .until(() -> adminDriver.getCurrentUrl().equals(UrlFactory.createWithRedirect(serverPort, Endpoints.INDEX_PAGE, Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE)))
            .assertTrue("User is not logged out");

        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        SleepUtil.sleep(3000);

        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> adminDriver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE))
            .assertTrue("User is not logged in");
    }

    private void addRole_emptyPassword(WebDriver adminDriver, RegistrationParameters testUserData) {
        RoleManagementActions.search(adminDriver, testUserData.getEmail());

        RoleManagementSearchResultUser testUser = AwaitilityWrapper.getOptionalWithWait(() -> RoleManagementActions.findUserByEmail(adminDriver, testUserData.getEmail()))
            .orElseThrow(() -> new RuntimeException("TestUser not found as search result."));

        testUser.getAvailableRoles()
            .get(0)
            .grant(adminDriver);

        RoleManagementActions.confirmGrantRole(adminDriver);

        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.EMPTY_PASSWORD);
    }
}
