package com.github.saphyra.apphub.ci.service.operations;

import com.github.saphyra.apphub.ci.service.operations.minikube.MinikubePortForwardService;
import com.github.saphyra.apphub.ci.service.operations.preprod.PreprodPortForwardService;
import com.github.saphyra.apphub.ci.service.operations.preprod.PreprodProxyService;
import com.github.saphyra.apphub.ci.service.operations.production.ProductionProxyService;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationsFacade {
    private final MinikubePortForwardService minikubePortForwardService;
    private final PreprodPortForwardService preprodPortForwardService;
    private final PreprodProxyService preprodProxyService;
    private final ProductionProxyService productionProxyService;

    public void portForward(Environment environment) {
        switch (environment) {
            case MINIKUBE -> minikubePortForwardService.portForward();
            case PREPROD -> preprodPortForwardService.portForward();
            default -> throw new IllegalArgumentException("Unsupported environment " + environment);
        }
    }

    public void proxy(Environment environment) {
        switch (environment){
            case PREPROD -> preprodProxyService.start();
            case PRODUCTION -> productionProxyService.start();
            default -> throw new IllegalArgumentException("Unsupported environment " + environment);
        }
    }
}
