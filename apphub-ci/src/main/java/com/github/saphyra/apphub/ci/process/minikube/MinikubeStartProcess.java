package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeStartProcess {
    @SneakyThrows
    public void startMinikube() {
        Process process = new ProcessBuilder("cmd", "/c", "minikube", "start", "&&", "start", "minikube", "dashboard")
            .inheritIO()
            .start();

        process.waitFor();
    }
}
