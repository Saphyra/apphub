package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStartServicesProcess {
    private final ValidatingInputReader validatingInputReader;
    private final Services services;
    private final LocalBuildTask localBuildTask;
    private final ProcessKiller processKiller;
    private final ServiceStarter serviceStarter;

    public void start() {
        String[] servicesToStart = validatingInputReader.getInput(
            LocalizedText.SERVICES_TO_START,
            input -> input.split(","),
            input -> Arrays.stream(input)
                .filter(serviceName -> services.getServices().stream().noneMatch(service -> service.getName().equals(serviceName)))
                .findAny()
                .map(serviceName -> language -> LocalizedText.SERVICE_NOT_FOUND.getLocalizedText(language).formatted(serviceName))
        );

        Arrays.stream(servicesToStart)
            .forEach(processKiller::killByServiceName);

        if (!localBuildTask.buildServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        serviceStarter.startServices(servicesToStart);
    }
}
