package com.github.saphyra.apphub.integration.frontend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.role_management.RoleManagementUser;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.AdminPanelPageActions;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.role_management.RoleManagementPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
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

        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.ADMIN_PANEL);

        AdminPanelPageActions.openRoleManagementPage(driver);

        RoleManagementUser user = RoleManagementPageActions.searchForUser(driver, testUserData.getEmail());
        user.addRole(Constants.ROLE_ADMIN);

        NotificationUtil.verifySuccessNotification(driver, "Jogosultság hozzáadva.");

        assertThat(user.getCurrentRoleNames()).contains(Constants.ROLE_ADMIN);
        assertThat(user.getAvailableRoleNames()).doesNotContain(Constants.ROLE_ADMIN);

        RoleManagementPageActions.back(driver);
        AdminPanelPageActions.back(driver);
        ModulesPageActions.logout(driver);

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(testUserData));
        assertThat(ModulesPageActions.getModule(driver, ModuleLocation.ADMIN_PANEL)).isPresent();
    }

    @Test
    public void removeRole() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, testUserData);
        DatabaseUtil.addRoleByEmail(testUserData.getEmail(), Constants.ROLE_ADMIN);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.ADMIN_PANEL);

        AdminPanelPageActions.openRoleManagementPage(driver);

        RoleManagementUser user = RoleManagementPageActions.searchForUser(driver, testUserData.getEmail());
        user.removeRole(Constants.ROLE_ADMIN);

        NotificationUtil.verifySuccessNotification(driver, "Jogosultság eltávolítva.");

        assertThat(user.getCurrentRoleNames()).doesNotContain(Constants.ROLE_ADMIN);
        assertThat(user.getAvailableRoleNames()).contains(Constants.ROLE_ADMIN);

        RoleManagementPageActions.back(driver);
        AdminPanelPageActions.back(driver);
        ModulesPageActions.logout(driver);

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(testUserData));
        assertThat(ModulesPageActions.getModule(driver, ModuleLocation.ADMIN_PANEL)).isEmpty();
    }
}
