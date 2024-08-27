package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.process.KillKubectlTask;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeStopProcess {
    private final KillKubectlTask killKubectlTask;

    @SneakyThrows
    public void stopMinikube() {
        new ProcessBuilder("minikube", "stop")
            .inheritIO()
            .start()
            .waitFor();

        killKubectlTask.run();
    }
}
