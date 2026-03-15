package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.process.KillKubectlTask;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeStopProcess {
    private final KillKubectlTask killKubectlTask;
    private final PlatformProperties platformProperties;
    private final ProcessKiller processKiller;

    @SneakyThrows
    public void stopMinikube() {
        new ProcessBuilder("minikube", "stop")
            .inheritIO()
            .start()
            .waitFor();

        killKubectlTask.run();
        processKiller.killByPort(platformProperties.getPreprodProxy().getPort());
        processKiller.killByPort(platformProperties.getProductionProxy().getPort());
    }
}
