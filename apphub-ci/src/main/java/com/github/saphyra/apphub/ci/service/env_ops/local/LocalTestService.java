package com.github.saphyra.apphub.ci.service.env_ops.local;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.service.env_ops.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.tool.ServicePinger;
import com.github.saphyra.apphub.ci.tool.TestRunner;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalTestService {
    private final Services services;
    private final ServicePinger servicePinger;
    private final PropertyDao propertyDao;
    private final IntegrationServerStarter integrationServerStarter;
    private final TestRunner testRunner;
    private final PlatformProperties platformProperties;

    public void runTests(String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        log.info("");
        log.info("Running tests again local environment with test filter: {}, threadCount: {}, preCreatedDriverCount: {}, retryCount: {}", testFilter.isEmpty() ? "All" : testFilter, threadCount, preCreatedDriverCount, retryCount);

        if (testFilter.isEmpty() && !allServicesRunning()) {
            throw new IllegalStateException("Services are not running.");
        } else if (!testFilter.isEmpty() && !enabledServicesAreRunning()) {
            throw new IllegalStateException("Enabled services are not running.");
        } else {
            log.info("Services are running.");
        }

        Optional<Exception> maybeFrontendFailure = servicePinger.singlePing("http://localhost:3000/");
        if (maybeFrontendFailure.isPresent()) {
            throw new IllegalStateException("Frontend is not running. " + maybeFrontendFailure.get().getMessage());
        } else {
            log.info("Frontend is running.");
        }

        integrationServerStarter.start();
        testRunner.runTests(
            Environment.LOCAL,
            testFilter,
            threadCount,
            platformProperties.getLocalServerPort(),
            platformProperties.getLocalDatabasePort(),
            platformProperties.getLocalDatabaseName(),
            "",
            preCreatedDriverCount,
            false,
            false,
            "",
            retryCount,
            "localhost:" + platformProperties.getLocalDynamoDbPort()
        );
    }

    private boolean allServicesRunning() {
        return services.getServices()
            .stream()
            .allMatch(service -> servicePinger.singlePingLocal(service.getPort()).isEmpty());
    }

    private boolean enabledServicesAreRunning() {
        List<String> disabledServices = propertyDao.getDisabledServices();

        return services.getServices()
            .stream()
            .filter(service -> !disabledServices.contains(service.getName()))
            .allMatch(service -> servicePinger.singlePingLocal(service.getPort()).isEmpty());
    }
}
