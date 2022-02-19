package com.github.saphyra.apphub.integraton.frontend.admin_panel;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.role_management.RoleManagementPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.admin_panel.RoleManagementUser;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleManagementTest extends SeleniumTest {
    @Test
    public void addRole() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, testUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.ROLE_MANAGEMENT);

        //Add role
        RoleManagementUser user = RoleManagementPageActions.searchForUser(driver, testUserData.getEmail());
        user.addRole(Constants.ROLE_TEST);
        NotificationUtil.verifySuccessNotification(driver, "Jogosultság hozzáadva.");
        assertThat(user.getCurrentRoleNames()).contains(Constants.ROLE_TEST);
        assertThat(user.getAvailableRoleNames()).doesNotContain(Constants.ROLE_TEST);

        //Remove role
        user = RoleManagementPageActions.searchForUser(driver, testUserData.getEmail());
        user.removeRole(Constants.ROLE_TEST);
        NotificationUtil.verifySuccessNotification(driver, "Jogosultság eltávolítva.");
        assertThat(user.getCurrentRoleNames()).doesNotContain(Constants.ROLE_TEST);
        assertThat(user.getAvailableRoleNames()).contains(Constants.ROLE_TEST);
    }
}
