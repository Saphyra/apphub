package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.DeployMode;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeBuildTask {
    private final PropertyDao propertyDao;
    private final Services services;

    public boolean installServices() {
        DeployMode deployMode = propertyDao.getRemoteDeployMode();
        if (deployMode == DeployMode.SKIP_BUILD) {
            return true;
        }

        String threadCount = String.valueOf(propertyDao.getThreadCount(deployMode));

        try {
            Process process = new ProcessBuilder("bash", "-c", "./infra/install_all.sh %s %s".formatted(threadCount, deployMode))
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

    public boolean installServices(List<String> serviceNames) {
        DeployMode deployMode = propertyDao.getRemoteDeployMode();
        if (deployMode == DeployMode.SKIP_BUILD) {
            return true;
        }

        String threadCount = String.valueOf(propertyDao.getThreadCount(deployMode));

        try {
            String moduleNames = serviceNames.stream()
                .map(serviceName -> ":" + services.findByNameValidated(serviceName).getModuleName())
                .collect(Collectors.joining(","));

            Process process = new ProcessBuilder("bash", "-c", "./infra/install_services.sh %s %s %s".formatted(threadCount, deployMode, moduleNames))
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

    public boolean deployServices(String username, String password) {
        String threadCount = String.valueOf(propertyDao.getThreadCount(DeployMode.DEFAULT));

        try {
            Process process = new ProcessBuilder("bash", "-c", "./infra/deploy_all.sh %s %s %s".formatted(threadCount, username, password))
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
