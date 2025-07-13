package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionStartProcess {
    private final MinikubeStartProcess minikubeStartProcess;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final StartProductionProxyProcess startProductionProxyProcess;
    private final MinikubeScaleProcess minikubeScaleProcess;

    public void startServer() {
        minikubeStartProcess.startMinikube();

        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, 60);

        startProductionProxyProcess.startProductionProxy();
    }
}
