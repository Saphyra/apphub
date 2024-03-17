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
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
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
        disableRole(driver, userData);

        enableRole_incorrectPassword(driver, userData);
        enableRole(driver, userData);
    }

    private static DisabledRole initialCheck(WebDriver driver) {
        DisabledRole initialRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);
        assertThat(initialRole.isEnabled()).isTrue();
        return initialRole;
    }

    private static void disableRole_incorrectPassword(WebDriver driver, RegistrationParameters userData, DisabledRole initialRole) {
        initialRole.disable(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.fillPassword(driver, "asd");
                DisabledRolesActions.confirmDisableRole(driver);
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
            });

        DisabledRolesActions.fillPassword(driver, "asd");
        DisabledRolesActions.confirmDisableRole(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(driver))
            .assertTrue("User is not logged out.");

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE))
            .assertTrue("Disabled role management page is not opened.");
    }

    private static void disableRole(WebDriver driver, RegistrationParameters userData) {
        DisabledRole initialRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);
        assertThat(initialRole.isEnabled()).isTrue();

        initialRole.disable(driver);

        DisabledRolesActions.fillPassword(driver, userData.getPassword());
        DisabledRolesActions.confirmDisableRole(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.DISABLED_ROLE_MANAGEMENT_ROLE_DISABLED);

        DisabledRole disabledRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);
        assertThat(disabledRole.isEnabled()).isFalse();
    }

    private static void enableRole_incorrectPassword(WebDriver driver, RegistrationParameters userData) {
        DisabledRole disabledRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);

        disabledRole.enable(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.fillPassword(driver, "asd");
                DisabledRolesActions.confirmEnableRole(driver);
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INCORRECT_PASSWORD);
            });

        DisabledRolesActions.fillPassword(driver, "asd");
        DisabledRolesActions.confirmEnableRole(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(driver))
            .assertTrue("User is not logged out.");

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE))
            .assertTrue("Disabled role management page is not opened.");
    }

    private static void enableRole(WebDriver driver, RegistrationParameters userData) {
        DisabledRole disabledRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);
        assertThat(disabledRole.isEnabled()).isFalse();

        disabledRole.enable(driver);

        DisabledRolesActions.fillPassword(driver, userData.getPassword());
        DisabledRolesActions.confirmEnableRole(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.DISABLED_ROLE_MANAGEMENT_ROLE_ENABLED);

        DisabledRole enabledRole = DisabledRolesActions.findRoleValidated(driver, Constants.ROLE_TEST);
        assertThat(enabledRole.isEnabled()).isTrue();
    }
}
