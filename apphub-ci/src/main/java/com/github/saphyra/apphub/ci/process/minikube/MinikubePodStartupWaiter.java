package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubePodStartupWaiter {
    private final PropertyDao propertyDao;

    @SneakyThrows
    public void waitForPods(String namespaceName) {
        int status = new ProcessBuilder(propertyDao.getBashFileLocation(), "-c", "./infra/wait_for_pods_ready.sh %s %s %s %s".formatted(namespaceName, 60, 2, 5))
            .inheritIO()
            .start()
            .waitFor();

        if (status != 0) {
            throw new IllegalStateException("Pods did not start up in time.");
        }
    }
}
