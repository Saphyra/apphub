package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class AccountPageRoleProtectionTest extends SeleniumTest {
    @Test(groups = {"fe", "account", "role-protection"})
    public void accountRoleProtection() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MANAGE_ACCOUNT);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        AccessTokenActions.invalidateAccessToken(driver, getServerPort());

        driver.navigate().to(UrlFactory.create(getServerPort(), UserEndpoints.ACCOUNT_PAGE));

        CommonUtils.verifyMissingRole(getServerPort(), driver.getCurrentUrl());
    }
}
