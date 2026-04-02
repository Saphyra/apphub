package com.github.saphyra.apphub.integration.backend.monitoring;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.MonitoringActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringTest extends BackEndTest {
    @Test(groups = {"be", "monitoring"})
    public void monitoringTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        assertThat(MonitoringActions.getFeatures(getServerPort(), accessTokenId)).contains(Feature.MEMORY_MONITORING);
        assertThat(MonitoringActions.getFunctionalities(getServerPort(), accessTokenId, Feature.MEMORY_MONITORING)).containsExactly(Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS);
        assertThat(MonitoringActions.getServices(getServerPort(), accessTokenId, Feature.MEMORY_MONITORING, Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS)).containsAll(Constants.SERVICES);
        Constants.SERVICES.forEach(service ->
            assertThat(MonitoringActions.getMetrics(
                getServerPort(),
                accessTokenId,
                Constants.MONITORING_METRIC_TYPE_SECOND,
                Feature.MEMORY_MONITORING,
                Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS,
                service
            ))
                .isNotEmpty()
        );
    }
}
