package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeNamespaceSetupTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionReleaseProcess {
    private final ValidatingInputReader validatingInputReader;
    private final MinikubeBuildTask minikubeBuildTask;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeNamespaceSetupTask minikubeNamespaceSetupTask;
    private final LocalStopProcess localStopProcess;
    private final StartProductionProxyProcess startProductionProxyProcess;

    public void release() {
        localStopProcess.stopServices();

        if (!minikubeBuildTask.deployServices()) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        minikubeNamespaceSetupTask.createNamespace(Constants.NAMESPACE_NAME_PRODUCTION);

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, 60);

        startProductionProxyProcess.startProductionProxy();
    }
}
