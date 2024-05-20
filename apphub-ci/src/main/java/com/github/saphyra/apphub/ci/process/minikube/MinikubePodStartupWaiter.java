package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubePodStartupWaiter {
    private final NamespaceNameProvider namespaceNameProvider;

    @SneakyThrows
    public void waitForPods() {
        new ProcessBuilder("bash", "-c", "./infra/wait_for_pods_ready.sh %s %s %s %s".formatted(namespaceNameProvider.getBranchName(), 60, 2, 5))
            .inheritIO()
            .start()
            .waitFor();
    }
}
