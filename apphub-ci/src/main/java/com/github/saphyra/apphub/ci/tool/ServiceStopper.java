package com.github.saphyra.apphub.ci.tool;

import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceStopper {
    private final ProcessKiller processKiller;
    private final PlatformProperties properties;
    private final Services services;

    public void stopLocalEnv() {
        stopLocalService(properties.getIntegrationServer());
        processKiller.killByPort(properties.getLocalDynamoDbPort());
        services.getServices()
            .forEach(this::stopLocalService);

        log.info("Local server stopped.");
    }

    public void stopLocalService(Service service) {
        log.info("Stopping service {}", service.getName());

        processKiller.killByPort(service.getPort());

        log.info("Service {} stopped.", service.getName());
    }
}
