package com.github.saphyra.apphub.integration.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CommunityRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "community"})
    public void communityRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.COMMUNITY);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        driver.navigate()
            .refresh();

        CommonUtils.verifyMissingRole(driver.getCurrentUrl());
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_COMMUNITY},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
