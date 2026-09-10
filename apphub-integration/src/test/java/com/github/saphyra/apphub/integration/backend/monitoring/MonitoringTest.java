package com.github.saphyra.apphub.integration.backend.monitoring;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.MonitoringActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringTest extends BackEndTest {
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
        assertThat(MonitoringActions.getServices(getServerPort(), accessToken, Feature.MEMORY_MONITORING, Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS)).containsAll(Constants.SERVICES);
        Constants.SERVICES.forEach(service ->
            assertThat(MonitoringActions.getMetrics(
                getServerPort(),
                accessToken,
                Constants.MONITORING_METRIC_TYPE_SECOND,
                Feature.MEMORY_MONITORING,
                Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS,
                service
            ))
                .isNotEmpty()
        );
    }
}
