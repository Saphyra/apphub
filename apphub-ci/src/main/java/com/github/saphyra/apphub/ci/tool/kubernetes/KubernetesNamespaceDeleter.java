package com.github.saphyra.apphub.ci.tool.kubernetes;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class KubernetesNamespaceDeleter {
    @SneakyThrows
    public void deleteNamespace(String namespaceName) {
        new ProcessBuilder("kubectl delete namespace %s".formatted(namespaceName).split(" "))
            .inheritIO()
            .start()
            .waitFor();
    }
}
