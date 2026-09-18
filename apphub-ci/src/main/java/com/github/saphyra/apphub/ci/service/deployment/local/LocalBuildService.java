package com.github.saphyra.apphub.ci.service.deployment.local;

import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalBuildService {
    void buildServices(List<Service> services, int buildThreadCount, boolean skipTests) {
        List<String> moduleNames = services.stream()
            .map(Service::getModuleName)
            .toList();

        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("mvn");
        command.add("-T");
        command.add(String.valueOf(buildThreadCount));
        command.add("clean");
        command.add("package");
        if (!moduleNames.isEmpty()) {
            command.add("-pl");
            command.add(moduleNames.stream().map(service -> ":" + service).collect(Collectors.joining(",")));
            command.add("-am");
        }
        if (skipTests) {
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
                return;
            } else {
                log.error("Build failed.");
                throw new RuntimeException("Build failed.");
            }
        } catch (Exception e) {
            log.error("Build failed with exception", e);
            throw new RuntimeException("Build failed with exception: " + e.getMessage(), e);
        }
    }
}
