package com.github.saphyra.apphub.ci.process.local.stop;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.utils.ObjectMapperWrapper;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.utils.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStopProcess {
    private final Services services;
    private final ProcessKiller processKiller;
    private final PlatformProperties testProperties;
    private final ExecutorServiceBean executorServiceBean;
    private final ValidatingInputReader validatingInputReader;
    private final PropertyDao propertyDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void stopAllServices() {
        log.info("Stopping local server...");

        stop(testProperties.getIntegrationServer());

        List<Service> services = this.services.getServices();

        stopServices(services);

        log.info("Local server stopped.");
    }

    private void stop(Service service) {
        log.info("Stopping service {}", service.getName());

        processKiller.killByPort(service.getPort());

        log.info("Service {} stopped.", service.getName());
    }

    public void stopServices() {
        String[] serviceNamesArr = validatingInputReader.getInput(
            LocalizedText.SERVICES_TO_STOP,
            input -> input.split(","),
            input -> Arrays.stream(input)
                .filter(serviceName -> services.getServices().stream().noneMatch(service -> service.getName().equals(serviceName)))
                .findAny()
                .map(serviceName -> language -> LocalizedText.SERVICE_NOT_FOUND.getLocalizedText(language).formatted(serviceName))
        );

        List<String> serviceNames = Arrays.asList(serviceNamesArr);

        propertyDao.save(PropertyName.LATEST_SERVICES, objectMapperWrapper.writeValueAsString(serviceNames));

        List<Service> servicesToStop = serviceNames.stream()
            .map(services::findByNameValidated)
            .toList();

        stopServices(servicesToStop);

        log.info("Services stopped.");
    }

    public void stopLatestServices() {
        List<String> serviceNames = propertyDao.getLatestServices();

        List<Service> servicesToStop = serviceNames.stream()
            .map(services::findByNameValidated)
            .toList();

        stopServices(servicesToStop);

        log.info("Services stopped.");
    }

    private void stopServices(List<Service> servicesToStop) {
        List<FutureWrapper<Void>> executionResults = servicesToStop.stream()
            .map(service -> executorServiceBean.execute(() -> stop(service)))
            .toList();

        executionResults.forEach(FutureWrapper::get);
    }
}
