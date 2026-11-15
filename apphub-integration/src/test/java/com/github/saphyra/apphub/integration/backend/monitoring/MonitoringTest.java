package com.github.saphyra.apphub.integration.backend.monitoring;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.MonitoringActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ServiceConstants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.monitoring.GetMetricsResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringTest extends BackEndTest {
    /**
     * Tests that monitoring endpoints return expected data for an admin user.
     *
     * <ul>
     *   <li>validate GET /monitoring/features returns MEMORY_MONITORING when logged in as admin</li>
     *   <li>validate GET /monitoring/functionality/MEMORY_MONITORING returns the memory status functionality</li>
     *   <li>validate GET /monitoring/services/MEMORY_MONITORING/{service} returns all services for the environment</li>
     *   <li>validate GET /monitoring/metrics/second/MEMORY_MONITORING/memory-status/{service} returns non‑empty metrics list for each service</li>
     * </ul>
     */
    @Test(groups = {"be", "monitoring"})
    public void monitoringTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        assertThat(MonitoringActions.getFeatures(getServerPort(), accessToken)).contains(Feature.MEMORY_MONITORING);
        assertThat(MonitoringActions.getFunctionalities(getServerPort(), accessToken, Feature.MEMORY_MONITORING)).containsExactly(Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS);
        assertThat(MonitoringActions.getServices(getServerPort(), accessToken, Feature.MEMORY_MONITORING, Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS)).containsAll(ServiceConstants.getServicesForEnvironment());
        ServiceConstants.getServicesForEnvironment()
            .forEach(service ->
                {
                    List<GetMetricsResponse> metrics = MonitoringActions.getMetrics(
                        getServerPort(),
                        accessToken,
                        Constants.MONITORING_METRIC_TYPE_SECOND,
                        Feature.MEMORY_MONITORING,
                        Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS,
                        service
                    );

                    if(metrics.isEmpty()){
                        throw new AssertionError("Metrics for service " + service + " is empty");
                    }
                }
            );
    }
}
