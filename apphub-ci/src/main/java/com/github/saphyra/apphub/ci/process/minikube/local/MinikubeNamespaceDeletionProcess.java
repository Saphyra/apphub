package com.github.saphyra.apphub.ci.process.minikube.local;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class MinikubeNamespaceDeletionProcess {
    @SneakyThrows
    public void deleteNamespace(String namespaceName) {
        new ProcessBuilder("kubectl delete namespace %s".formatted(namespaceName).split(" "))
            .inheritIO()
            .start()
            .waitFor();
    }
}
