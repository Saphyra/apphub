package com.github.saphyra.apphub.ci.tool;

import com.github.saphyra.apphub.ci.process.DeprecatedProcessKiller;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesStopper {
    private final KubectlKiller kubectlKiller;
    private final PlatformProperties platformProperties;
    private final DeprecatedProcessKiller processKiller;

    @SneakyThrows
    public void stopMinikube() {
        new ProcessBuilder("minikube", "stop")
            .inheritIO()
            .start()
            .waitFor();

        kubectlKiller.kill();
        processKiller.killByPort(platformProperties.getPreprodProxy().getPort());
        processKiller.killByPort(platformProperties.getProductionProxy().getPort());
    }
}
