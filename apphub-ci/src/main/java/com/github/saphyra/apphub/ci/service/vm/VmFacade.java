package com.github.saphyra.apphub.ci.service.vm;

import com.github.saphyra.apphub.ci.service.vm.minikube.MinikubeNamespaceDeletionService;
import com.github.saphyra.apphub.ci.service.vm.production.ProductionStartVmService;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesStarter;
import com.github.saphyra.apphub.ci.tool.kubernetes.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VmFacade {
    private final KubernetesStarter kubernetesStarter;
    private final MinikubeNamespaceDeletionService minikubeNamespaceDeletionService;
    private final ProductionStartVmService productionStartVmService;
    private final NamespaceNameProvider namespaceNameProvider;
    private final StopVmService stopVmService;

    public void start(Environment environment) {
        switch (environment) {
            case MINIKUBE, PREPROD -> kubernetesStarter.start();
            case PRODUCTION -> productionStartVmService.start();
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    public void stop(Environment environment) {
        switch (environment) {
            case MINIKUBE -> stopVmService.stop(namespaceNameProvider.getNamespaceName());
            case PREPROD -> stopVmService.stop(Constants.NAMESPACE_NAME_PREPROD);
            case PRODUCTION -> stopVmService.stop(Constants.NAMESPACE_NAME_PRODUCTION);
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
