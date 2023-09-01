package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.disabled_roles.DisabledRolesActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.DisabledRole;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleManagementCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "admin-panel"})
    public void disableAndEnableRole() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.DISABLED_ROLE_MANAGEMENT);

        DisabledRole initialRole = initialCheck(driver);
        disableRole_incorrectPassword(driver, userData, initialRole);
        DisabledRole disabledRole = getDisabledRole(driver, userData);
        enableRole_incorrectPassword(driver, disabledRole);
        enableRole(driver, userData);
    }

    private static DisabledRole initialCheck(WebDriver driver) {
        DisabledRole initialRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(disabledRole -> disabledRole.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(initialRole.isEnabled()).isTrue();
        return initialRole;
    }

    private static void disableRole_incorrectPassword(WebDriver driver, RegistrationParameters userData, DisabledRole initialRole) {
        initialRole.toggle(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
                DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
                NotificationUtil.verifyErrorNotification(driver, "Incorrect password.");
                assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();
            });

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Account locked. Try again later.");

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(driver))
            .assertTrue("User is not logged out.");

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE))
            .assertTrue("Disabled role management page is not opened.");
    }

    private static DisabledRole getDisabledRole(WebDriver driver, RegistrationParameters userData) {
        DisabledRole initialRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(disabledRole -> disabledRole.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(initialRole.isEnabled()).isTrue();
        initialRole.toggle(driver);

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, userData.getPassword());
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Role disabled.");

        DisabledRole disabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(disabledRole.isEnabled()).isFalse();

        NotificationUtil.clearNotifications(driver);
        return disabledRole;
    }

    private static void enableRole_incorrectPassword(WebDriver driver, DisabledRole disabledRole) {
        disabledRole.toggle(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
                DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
                NotificationUtil.verifyErrorNotification(driver, "Incorrect password.");
                assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();
            });

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Account locked. Try again later.");

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(driver))
            .assertTrue("User is not logged out.");
    }

    private static void enableRole(WebDriver driver, RegistrationParameters userData) {
        DisabledRole disabledRole;
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE))
            .assertTrue("Disabled role management page is not opened.");

        disabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(disabledRole.isEnabled()).isFalse();

        NotificationUtil.clearNotifications(driver);
        disabledRole.toggle(driver);

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, userData.getPassword());
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Role enabled.");

        DisabledRole enabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(enabledRole.isEnabled()).isTrue();
    }
}
