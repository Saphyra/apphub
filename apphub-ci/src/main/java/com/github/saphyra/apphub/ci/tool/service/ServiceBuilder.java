package com.github.saphyra.apphub.ci.tool.service;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.BuildCommand;
import com.github.saphyra.apphub.ci.value.DockerTag;
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
public class ServiceBuilder {
    private final PropertyDao propertyDao;

    /**
     * Builds the provided services locally
     */
    public void build(List<Service> services, int buildThreadCount, boolean skipTests) {
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
            throw new RuntimeException("Build failed with exception: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a docker image for the provided services
     */
    public void build(BuildCommand buildCommand, List<Service> services, int buildThreadCount, boolean skipTests) {
        try {
            String moduleNames = services.stream()
                .map(Service::getModuleName)
                .map(moduleName -> ":" + moduleName)
                .collect(Collectors.joining(","));
            log.info("ModuleNames: {}", moduleNames);

            Process process = new ProcessBuilder(propertyDao.getBashFileLocation(), "-c", "./infra/build_services.sh %s %s %s %s".formatted(buildCommand.name().toLowerCase(), buildThreadCount, skipTests, moduleNames))
                .inheritIO()
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Build successful!");
            } else {
                throw new RuntimeException("Build failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Build failed", e);
        }
    }

    /**
     * Builds the frontend and creates a docker image with the provided tag from it
     */
    public void buildFrontend(DockerTag tag) {
        try {
            Process process = new ProcessBuilder(propertyDao.getBashFileLocation(), "-c", "./infra/build_frontend.sh %s".formatted(tag.name().toLowerCase()))
                .inheritIO()
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Build successful!");
            } else {
                throw new RuntimeException("Build failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Build failed", e);
        }
    }
}
