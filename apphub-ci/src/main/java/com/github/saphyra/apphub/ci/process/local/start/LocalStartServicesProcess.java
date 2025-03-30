package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.utils.ObjectMapperWrapper;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStartServicesProcess {
    private final ValidatingInputReader validatingInputReader;
    private final Services services;
    private final LocalBuildTask localBuildTask;
    private final ProcessKiller processKiller;
    private final ServiceStarter serviceStarter;
    private final PropertyDao propertyDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void start() {
        String[] servicesToStartArr = validatingInputReader.getInput(
            LocalizedText.SERVICES_TO_START,
            input -> input.split(","),
            input -> Arrays.stream(input)
                .filter(serviceName -> services.getServices().stream().noneMatch(service -> service.getName().equals(serviceName)))
                .findAny()
                .map(serviceName -> language -> LocalizedText.SERVICE_NOT_FOUND.getLocalizedText(language).formatted(serviceName))
        );

        List<String> servicesToStart = Arrays.asList(servicesToStartArr);

        startServices(servicesToStart);

        propertyDao.save(PropertyName.LATEST_SERVICES, objectMapperWrapper.writeValueAsString(servicesToStart));
    }

    public void startLatestServices() {
        startServices(propertyDao.getLatestServices());
    }

    private void startServices(List<String> servicesToStart) {
        servicesToStart.forEach(processKiller::killByServiceName);

        if (!localBuildTask.buildServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        serviceStarter.startServices(servicesToStart);
    }
}
