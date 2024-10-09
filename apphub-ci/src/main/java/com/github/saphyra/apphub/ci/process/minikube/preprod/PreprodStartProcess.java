package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeNamespaceSetupTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStartProcess {
    private final MinikubeStartProcess minikubeStartProcess;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final MinikubeNamespaceSetupTask minikubeNamespaceSetupTask;
    private final PortForwardTask portForwardTask;
    private final PlatformProperties platformProperties;

    public void startServer() {
        minikubeStartProcess.startMinikube();

        minikubeNamespaceSetupTask.deployPostgres(Constants.NAMESPACE_NAME_PREPROD);

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD);

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
    }
}
