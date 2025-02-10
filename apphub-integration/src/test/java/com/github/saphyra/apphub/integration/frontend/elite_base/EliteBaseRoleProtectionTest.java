package com.github.saphyra.apphub.integration.frontend.elite_base;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EliteBaseRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "elite-base"})
    public void eliteBaseRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, EliteBaseEndpoints.ELITE_BASE_PAGE);
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ELITE_BASE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
