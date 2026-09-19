package com.github.saphyra.apphub.ci.service.vm;

import com.github.saphyra.apphub.ci.service.vm.minikube.MinikubeNamespaceDeletionService;
import com.github.saphyra.apphub.ci.service.vm.minikube.MinikubeStopVmService;
import com.github.saphyra.apphub.ci.tool.KubernetesStarter;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VmFacade {
    private final KubernetesStarter kubernetesStarter;
    private final MinikubeStopVmService minikubeStopVmService;
    private final MinikubeNamespaceDeletionService minikubeNamespaceDeletionService;

    public void start(Environment environment) {
        switch (environment) {
            case MINIKUBE -> kubernetesStarter.start();
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    public void stop(Environment environment) {
        switch (environment) {
            case MINIKUBE -> minikubeStopVmService.stop();
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    public void deleteNamespace(Environment environment) {
        if (environment == Environment.MINIKUBE) {
            minikubeNamespaceDeletionService.delete();
        } else {
            throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }
}
