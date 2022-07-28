package com.github.saphyra.apphub.integraton.frontend.admin_panel;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.disabled_roles.DisabledRolesActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.admin_panel.DisabledRole;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

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

        //Disable role - Incorrect password
        initialRole.toggle(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
                DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
                NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
                assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();
            });

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Fiók zárolva. Próbáld újra később!");

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ModulesPageActions.openModule(driver, ModuleLocation.DISABLED_ROLE_MANAGEMENT);

        //Disable role
        initialRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(disabledRole -> disabledRole.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(initialRole.isEnabled()).isTrue();
        initialRole.toggle(driver);

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

        //Enable role - Incorrect password
        disabledRole.toggle(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
                DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
                NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
                assertThat(DisabledRolesActions.isToggleDisabledRoleConfirmationDialogOpened(driver)).isTrue();
            });

        DisabledRolesActions.enterPasswordToDisabledRoleToggleConfirmationDialog(driver, "asd");
        DisabledRolesActions.confirmDisabledRoleToggleConfirmationDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "Fiók zárolva. Próbáld újra később!");

        //Enable role
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));
        ModulesPageActions.openModule(driver, ModuleLocation.DISABLED_ROLE_MANAGEMENT);

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
        NotificationUtil.verifySuccessNotification(driver, "Jogosultság engedélyezve.");

        DisabledRole enabledRole = DisabledRolesActions.getDisabledRoles(driver)
            .stream()
            .filter(role -> role.getRole().equals(Constants.TEST_ROLE_NAME))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Test role not found"));
        assertThat(enabledRole.isEnabled()).isTrue();
    }
}
