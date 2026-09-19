package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionDeployProcess {
    private final MinikubeBuildTask minikubeBuildTask;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final LocalStopProcess localStopProcess;
    private final StartProductionProxyProcess startProductionProxyProcess;

    public void deploy() {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.deployServices()) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        kubernetesNamespaceSetupper.setupNamespace(Environment.PRODUCTION, Constants.NAMESPACE_NAME_PRODUCTION);

        //kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, 60);

        startProductionProxyProcess.startProductionProxy();
    }
}
