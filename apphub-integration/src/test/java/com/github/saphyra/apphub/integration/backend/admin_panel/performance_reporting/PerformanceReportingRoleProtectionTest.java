package com.github.saphyra.apphub.integration.backend.admin_panel.performance_reporting;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.PerformanceReportingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class PerformanceReportingRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "admin-panel"})
    public void performanceReportingRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> PerformanceReportingActions.getEnableResponse(getServerPort(), accessTokenId, PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING));
        CommonUtils.verifyMissingRole(() -> PerformanceReportingActions.getDisableResponse(getServerPort(), accessTokenId, PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING));
        CommonUtils.verifyMissingRole(() -> PerformanceReportingActions.getReportsResponse(getServerPort(), accessTokenId, PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING));
        CommonUtils.verifyMissingRole(() -> PerformanceReportingActions.getDeleteAllResponse(getServerPort(), accessTokenId, PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
