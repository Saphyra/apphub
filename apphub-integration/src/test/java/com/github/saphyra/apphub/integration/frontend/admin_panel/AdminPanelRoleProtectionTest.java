package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AdminPanelRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "admin-panel"})
    public void adminPanelRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_BAN_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_MEMORY_MONITORING_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_MIGRATION_TASKS_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_ROLE_MANAGEMENT_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.ADMIN_PANEL_ROLES_FOR_ALL_PAGE);
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
