package com.github.saphyra.apphub.ci.process.local.run_tests;

import com.github.saphyra.apphub.ci.process.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.process.RunTestsTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalRunTestsProcess {
    private final ServicePinger servicePinger;
    private final Services services;
    private final IntegrationServerStarter integrationServerStarter;
    private final RunTestsTask runTestsTask;

    public void run(){
        run("");
    }

    public void run(String testGroups) {
        log.info("");
        log.info("Running tests again local environment...");

        if (services.getServices().stream().anyMatch(service -> servicePinger.singlePingLocal(service.getPort()).isPresent())) {
            log.error("Services are not running.");
            return;
        }

        integrationServerStarter.start();
        runTestsTask.localRunTests(testGroups);
    }
}
