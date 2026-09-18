package com.github.saphyra.apphub.ci.service.deployment.local;

import com.github.saphyra.apphub.ci.tool.ServiceStarter;
import com.github.saphyra.apphub.ci.tool.ServiceStopper;
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
    private final LocalBuildService localBuildService;
    private final ServiceStarter serviceStarter;

    public void deploy(List<Service> services, int buildThreadCount, int startupCountLimit, boolean skipTests) {
        localDynamoDbStarter.startDynamoDb();
        serviceStopper.stopLocalEnv();
        localBuildService.buildServices(services, buildThreadCount, skipTests);
        serviceStarter.startServices(services, startupCountLimit, Map.of("SPRING_ACTIVE_PROFILE", Constants.PROFILE_LOCAL));
    }
}
