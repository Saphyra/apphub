package com.github.saphyra.apphub.ci.process.local.stop;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.utils.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStopProcess {
    private final Services services;
    private final ProcessKiller processKiller;
    private final PlatformProperties testProperties;
    private final ExecutorServiceBean executorServiceBean;

    public void stopServices() {
        log.info("Stopping local server...");

        stop(testProperties.getIntegrationServer());

        List<FutureWrapper<Void>> executionResults = services.getServices()
            .stream()
            .map(service -> executorServiceBean.execute(() -> stop(service)))
            .toList();

        executionResults.forEach(FutureWrapper::get);

        log.info("Local server stopped.");
    }

    private void stop(Service service) {
        log.info("Stopping service {}", service.getName());

        processKiller.killByPort(service.getPort());

        log.info("Service {} stopped.", service.getName());
    }
}
