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
        deployPostgres(namespaceName);
        minikubePodStartupWaiter.waitForPods(namespaceName);
    }

    @SneakyThrows
    public void createNamespace(String namespaceName) {
        new ProcessBuilder("kubectl", "create", "namespace", namespaceName)
            .inheritIO()
            .start()
            .waitFor();
    }

    @SneakyThrows
    public void deployPostgres(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/persistent-volume.yaml")
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-postgres.yaml")
            .inheritIO()
            .start()
            .waitFor();
    }
}
