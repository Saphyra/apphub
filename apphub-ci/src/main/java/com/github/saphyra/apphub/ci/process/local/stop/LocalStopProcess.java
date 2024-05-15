package com.github.saphyra.apphub.ci.process.local.stop;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import com.github.saphyra.apphub.ci.value.TestProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStopProcess {
    private final Services services;
    private final ProcessKiller processKiller;
    private final TestProperties testProperties;

    public void run() {
        log.info("Stopping local server...");

        stop(testProperties.getIntegrationServer());

        services.getServices()
            .forEach(this::stop);

        log.info("Local server stopped.");
    }

    private void stop(Service service) {
        log.info("Stopping service {}", service.getName());

        processKiller.killByPort(service.getPort());

        log.info("Service {} stopped.", service.getName());
    }
}
