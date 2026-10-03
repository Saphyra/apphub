package com.github.saphyra.apphub.ci.tool.kubernetes;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesStarter {
    @SneakyThrows
    public void start() {
        Process process = new ProcessBuilder("cmd", "/c", "minikube", "start", "&&", "start", "minikube", "dashboard")
            .inheritIO()
            .start();

        process.waitFor();
    }
}
