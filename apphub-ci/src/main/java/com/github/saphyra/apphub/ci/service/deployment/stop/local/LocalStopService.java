package com.github.saphyra.apphub.ci.service.deployment.stop.local;

import com.github.saphyra.apphub.ci.tool.ProcessKiller;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStopService {
    private final ProcessKiller processKiller;
    private final ExecutorServiceBean executorServiceBean;
    private final PlatformProperties properties;

    private void stop(Service service) {
        log.info("Stopping service {}", service.getName());

        processKiller.killByPort(service.getPort());

        log.info("Service {} stopped.", service.getName());
    }

    public void stopServices(List<Service> services) {
        log.info("Stopping local server...");

        stop(properties.getIntegrationServer());
        processKiller.killByPort(properties.getLocalDynamoDbPort());

        List<FutureWrapper<Void>> executionResults = services.stream()
            .map(service -> executorServiceBean.execute(() -> stop(service)))
            .toList();

        executionResults.forEach(FutureWrapper::get);

        log.info("Services stopped.");
    }
}
