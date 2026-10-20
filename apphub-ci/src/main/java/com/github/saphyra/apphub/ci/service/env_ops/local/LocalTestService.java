package com.github.saphyra.apphub.ci.service.env_ops.local;

import com.github.saphyra.apphub.ci.dao.property.PropertyDao;
import com.github.saphyra.apphub.ci.tool.service.ServicePinger;
import com.github.saphyra.apphub.ci.tool.test.TestRunner;
import com.github.saphyra.apphub.ci.tool.test.TestRunner.TestConfiguration;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
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
    private final TestRunner testRunner;
    private final PlatformProperties platformProperties;

    public void runTests(String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        log.info("");
        log.info("Running tests again local environment with test filter: {}, threadCount: {}, preCreatedDriverCount: {}, retryCount: {}", testFilter.isEmpty() ? "All" : testFilter, threadCount, preCreatedDriverCount, retryCount);

        if (testFilter.isEmpty()) {
            List<String> unreachableServices = allServicesRunning();
            if (!unreachableServices.isEmpty()) {
                throw new IllegalStateException("Services are not running. Missing services: " + unreachableServices);
            }
        } else {
            List<String> unreachableServices = enabledServicesAreRunning();
            if (!unreachableServices.isEmpty()) {
                throw new IllegalStateException("Enabled services are not running. Missing services: " + unreachableServices);
            }
        }

        log.info("Services are running.");

        Optional<Exception> maybeFrontendFailure = servicePinger.singlePing("http://localhost:3000/");
        if (maybeFrontendFailure.isPresent()) {
            throw new IllegalStateException("Frontend is not running. " + maybeFrontendFailure.get().getMessage());
        } else {
            log.info("Frontend is running.");
        }

        testRunner.runTests(
            TestConfiguration.builder()
                .environment(Environment.LOCAL)
                .testFilter(testFilter)
                .threadCount(threadCount)
                .serverPort(platformProperties.getLocalServerPort())
                .databasePort(platformProperties.getLocalDatabasePort())
                .databaseName(platformProperties.getLocalDatabaseName())
                .disabledGroups("")
                .preCreateDrivers(preCreatedDriverCount)
                .serverConnectionCacheEnabled(false)
                .databaseConnectionCacheEnabled(false)
                .namespace("")
                .retryCount(retryCount)
                .dynamoDbHost("localhost:" + platformProperties.getLocalDynamoDbPort())
                .build()
        );
    }

    private List<String> allServicesRunning() {
        return services.getServices()
            .stream()
            .filter(service -> servicePinger.singlePingLocal(service.getPort()).isPresent())
            .map(Service::getName)
            .toList();
    }

    /**
     *
     * @return missing services
     */
    private List<String> enabledServicesAreRunning() {
        List<String> disabledServices = propertyDao.getDisabledServices();

        return services.getServices()
            .stream()
            .filter(service -> !disabledServices.contains(service.getName()))
            .filter(service -> servicePinger.singlePingLocal(service.getPort()).isPresent())
            .map(Service::getName)
            .toList();
    }
}
