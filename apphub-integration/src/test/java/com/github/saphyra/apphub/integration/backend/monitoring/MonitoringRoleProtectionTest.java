package com.github.saphyra.apphub.integration.backend.monitoring;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.MonitoringActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class MonitoringRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "monitoring", "role-protection"})
    public void monitoringRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetFeaturesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetFunctionalitiesResponse(getServerPort(), accessTokenId, Feature.ELITE_BASE_MESSAGE_PROCESSING));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetServicesResponse(getServerPort(), accessTokenId, Feature.ELITE_BASE_MESSAGE_PROCESSING, "asd"));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetMetricsResponse(getServerPort(), accessTokenId, "asd", Feature.ELITE_BASE_MESSAGE_PROCESSING, "asd", "asd"));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
