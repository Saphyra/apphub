package com.github.saphyra.apphub.integration.frontend.monitoring;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.endpoints.MonitoringEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MonitoringRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "monitoring", "role-protection"})
    public void monitoringRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, MonitoringEndpoints.MONITORING_PAGE);
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
