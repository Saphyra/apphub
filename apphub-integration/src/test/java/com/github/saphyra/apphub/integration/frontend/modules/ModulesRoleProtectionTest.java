package com.github.saphyra.apphub.integration.frontend.modules;

import com.github.saphyra.apphub.integration.action.frontend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ModulesRoleProtectionTest extends SeleniumTest {
    @Test(groups = {"fe", "modules", "role-protection"})
    public void modulesRoleProtection() {
        WebDriver driver = extractDriver();

        int serverPort = getServerPort();
        Navigation.toIndexPage(serverPort, driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        AccessTokenActions.invalidateAccessToken(driver, serverPort);

        AwaitilityWrapper.awaitAssert(() -> CommonUtils.verifyMissingRole(serverPort, driver.getCurrentUrl()));
    }
}
