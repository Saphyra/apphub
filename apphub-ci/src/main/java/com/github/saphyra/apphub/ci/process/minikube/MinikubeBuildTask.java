package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.DeployMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeBuildTask {
    private final PropertyDao propertyDao;

    public boolean buildServices() {
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

    public boolean buildServices(String[] serviceNames) {
        //TODO implement
        return true;
    }
}
