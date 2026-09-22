package com.github.saphyra.apphub.ci.service.env_ops.local;

import com.github.saphyra.apphub.ci.tool.service.ServiceBuilder;
import com.github.saphyra.apphub.ci.tool.service.ServiceStarter;
import com.github.saphyra.apphub.ci.tool.service.ServiceStopper;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocalDeploymentService {
    private final LocalDynamoDbStarter localDynamoDbStarter;
    private final ServiceStopper serviceStopper;
    private final ServiceBuilder serviceBuilder;
    private final ServiceStarter serviceStarter;

    public void deploy(boolean startUserDefinedServices, List<Service> services, int buildThreadCount, int startupCountLimit, boolean skipTests) {
        if (startUserDefinedServices) {
            services.forEach(serviceStopper::stopLocalService);
        } else {
            serviceStopper.stopLocalEnv();
        }
        localDynamoDbStarter.startDynamoDb();
        serviceBuilder.build(services, buildThreadCount, skipTests);
        serviceStarter.startServices(services, startupCountLimit, Map.of("SPRING_ACTIVE_PROFILE", Constants.PROFILE_LOCAL));
    }
}
