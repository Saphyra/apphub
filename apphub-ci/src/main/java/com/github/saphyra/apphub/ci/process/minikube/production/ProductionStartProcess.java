package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.KubernetesStarter;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated
public class ProductionStartProcess {
    private final KubernetesStarter kubernetesStarter;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final StartProductionProxyProcess startProductionProxyProcess;
    private final KubernetesPodScaler kubernetesPodScaler;

    public void startServer() {
        kubernetesStarter.start();

        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        //kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, 60);

        startProductionProxyProcess.startProductionProxy();
    }
}
