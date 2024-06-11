package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.DeployMode;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalBuildTask {
    private final PropertyDao propertyDao;
    private final Services services;

    public boolean buildServices() {
        List<String> disabledServices = propertyDao.getDisabledServices();

        return buildServices(services.getServices().stream().filter(service -> !disabledServices.contains(service.getName())).map(Service::getModuleName).toList());
    }

    public boolean buildServices(String[] serviceNames) {
        List<String> moduleNames = Arrays.stream(serviceNames)
            .map(services::findByNameValidated)
            .map(Service::getModuleName)
            .toList();

        return buildServices(moduleNames);
    }

    /**
     * @return true, if maven build is successful, false otherwise.
     */
    public boolean buildServices(List<String> moduleNames) {
        DeployMode deployMode = propertyDao.getLocalDeployMode();
        if (deployMode == DeployMode.SKIP_BUILD) {
            return true;
        }

        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("mvn");
        command.add("-T");
        command.add(String.valueOf(propertyDao.getThreadCount(deployMode)));
        command.add("clean");
        command.add("package");
        if (!moduleNames.isEmpty()) {
            command.add("-pl");
            command.add(moduleNames.stream().map(service -> ":" + service).collect(Collectors.joining(",")));
            command.add("-am");
        }
        if (deployMode == DeployMode.SKIP_TESTS) {
            command.add("-DskipTests");
        }

        log.info("Command line: {}", String.join(" ", command));

        String[] array = new String[command.size()];

        try {
            Process process = new ProcessBuilder(command.toArray(array))
                .inheritIO()
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Build successful!");
                return true;
            } else {
                log.error("Build failed.");
                return false;
            }
        } catch (Exception e) {
            log.error("Build failed with exception", e);
            return false;
        }
    }
}
