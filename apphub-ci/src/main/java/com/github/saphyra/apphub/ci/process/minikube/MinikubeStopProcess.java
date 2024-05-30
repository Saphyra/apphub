package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeStopProcess {
    @SneakyThrows
    public void stopMinikube() {
        new ProcessBuilder("minikube", "stop")
            .inheritIO()
            .start()
            .waitFor();
    }
}
