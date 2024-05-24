package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MinikubeNamespaceSetupTask {
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;

    @SneakyThrows
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

    private static void deployPostgres(String namespaceName) throws InterruptedException, IOException {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deployment/persistent-volume.yaml")
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deployment/deploy-postgres.yaml")
            .inheritIO()
            .start()
            .waitFor();
    }
}
