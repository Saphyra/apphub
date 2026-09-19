package com.github.saphyra.apphub.ci.process.minikube.local;

import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
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
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final NamespaceNameProvider namespaceNameProvider;
    private final PlatformProperties platformProperties;

    public void deploy(List<String> servicesToStart) {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        String namespaceName = namespaceNameProvider.getNamespaceName();

        kubernetesServiceDeployer.deploy(namespaceName, Constants.DIR_NAME_DEVELOP, List.of(), 15, 1);

        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_DYNAMO_DB, platformProperties.getMinikubeDynamoDbPort(), platformProperties.getLocalDynamoDbPort());

        log.info("Deployment finished.");
    }

    public void deploy() {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices()) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        String namespaceName = namespaceNameProvider.getNamespaceName();

        kubernetesPodScaler.scaleAll(namespaceName, 0);

        kubernetesNamespaceSetupper.setupNamespace(Environment.MINIKUBE, namespaceName);
        kubernetesNamespaceSetupper.deployPostgres(namespaceName);
        kubernetesNamespaceSetupper.deployDynamoDb(namespaceName);

        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_DYNAMO_DB, platformProperties.getMinikubeDynamoDbPort(), platformProperties.getLocalDynamoDbPort());

        log.info("Deployment finished.");
    }
}
