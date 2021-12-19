package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.SleepUtil;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.disabled_role_management.DisabledRole;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.disabled_roles.DisabledRolesActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleManagementCrudTest extends SeleniumTest {
    @Test
    public void disableAndEnableRole() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.DISABLED_ROLE_MANAGEMENT);

        //Initial check
        DisabledRole initialRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(disabledRole -> disabledRole.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(initialRole.isEnabled()).isTrue();

        //Disable role
        initialRole.toggle(driver);

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
        assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, userData.getPassword());
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Jogosultság letiltva.");

        DisabledRole disabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(disabledRole.isEnabled()).isFalse();

        NotificationUtil.clearNotifications(driver);

        //Enable role
        disabledRole.toggle(driver);

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
        assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, userData.getPassword());
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Jogosultság engedélyezve.");

        DisabledRole enabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(enabledRole.isEnabled()).isTrue();
    }
}
