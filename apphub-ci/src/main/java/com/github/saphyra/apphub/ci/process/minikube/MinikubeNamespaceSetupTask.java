package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeNamespaceSetupTask {
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;

    public void setupNamespace(String namespaceName) {
        createNamespace(namespaceName);
    }

    @SneakyThrows
    private void createNamespace(String namespaceName) {
        new ProcessBuilder("kubectl", "create", "namespace", namespaceName)
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deployment/persistent-volume.yaml")
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deployment/deploy-postgres.yaml")
            .inheritIO()
            .start()
            .waitFor();

        minikubePodStartupWaiter.waitForPods();
    }
}
