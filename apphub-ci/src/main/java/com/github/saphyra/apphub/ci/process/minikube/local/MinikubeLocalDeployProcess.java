package com.github.saphyra.apphub.ci.process.minikube.local;

import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeNamespaceSetupTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeLocalDeployProcess {
    private final LocalStopProcess localStopProcess;
    private final MinikubeBuildTask minikubeBuildTask;
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeNamespaceSetupTask minikubeNamespaceSetupTask;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final PortForwardTask portForwardTask;
    private final NamespaceNameProvider namespaceNameProvider;
    private final PlatformProperties platformProperties;

    public void deploy(List<String> servicesToStart){
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        String namespaceName = namespaceNameProvider.getNamespaceName();

        minikubeServiceDeployer.deploy(namespaceName, Constants.DIR_NAME_DEVELOP, servicesToStart, 15);

        portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);

        log.info("Deployment finished.");
    }

    public void deploy() {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices()) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        String namespaceName = namespaceNameProvider.getNamespaceName();

        minikubeScaleProcess.scale(namespaceName, 0);

        minikubeNamespaceSetupTask.setupNamespace(namespaceName);

        minikubeServiceDeployer.deploy(namespaceName, "develop", 15);

        portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);

        log.info("Deployment finished.");
    }
}
