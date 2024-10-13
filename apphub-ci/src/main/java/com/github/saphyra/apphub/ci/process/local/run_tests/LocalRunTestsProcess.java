package com.github.saphyra.apphub.ci.process.local.run_tests;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.process.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.process.RunTestsTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalRunTestsProcess {
    private final ServicePinger servicePinger;
    private final Services services;
    private final IntegrationServerStarter integrationServerStarter;
    private final RunTestsTask runTestsTask;
    private final PropertyDao propertyDao;

    public void run() {
        run("");
    }

    public void run(String testGroups) {
        log.info("");
        log.info("Running tests again local environment...");

        if (testGroups.isEmpty() && !allServicesRunning()) {
            log.error("Services are not running.");
            return;
        } else if (!testGroups.isEmpty() && !enabledServicesAreRunning()) {
            log.error("Enabled services are not running.");
            return;
        } else {
            log.info("Services are running.");
        }

        Optional<Exception> maybeFrontendFailure = servicePinger.singlePing("http://localhost:3000/");
        if (maybeFrontendFailure.isPresent()) {
            log.error("Frontend is not running. {}", maybeFrontendFailure.get().getMessage());
            return;
        }

        integrationServerStarter.start();
        runTestsTask.localRunTests(testGroups);
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
