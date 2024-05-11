package com.github.saphyra.apphub.integration.frontend.modules;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ModulesRoleProtectionTest extends SeleniumTest {
    @Test(groups = {"fe", "modules"})
    public void modulesRoleProtection() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        SleepUtil.sleep(3000);

        driver.navigate()
            .refresh();

        CommonUtils.verifyMissingRole(driver.getCurrentUrl());
    }
}
