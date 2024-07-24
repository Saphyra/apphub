package com.github.saphyra.apphub.integration.frontend.villany_atesz;

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

public class VillanyAteszRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "villany-atesz"})
    public void villanyAteszRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(driver, Endpoints.VILLANY_ATESZ_PAGE);
        CommonUtils.verifyMissingRole(driver, Endpoints.VILLANY_ATESZ_CONTACTS_PAGE);
        CommonUtils.verifyMissingRole(driver, Endpoints.VILLANY_ATESZ_STOCK_PAGE);
        CommonUtils.verifyMissingRole(driver, Endpoints.VILLANY_ATESZ_TOOLBOX_PAGE);
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_VILLANY_ATESZ},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
