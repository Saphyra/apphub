package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.util.DatabaseUtil;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.DeployMode;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class PreprodDeployProcess {
    private final MinikubeBuildTask minikubeBuildTask;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final LocalStopProcess localStopProcess;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final PlatformProperties platformProperties;

    public void deploy() {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices(DeployMode.DEFAULT)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PREPROD, 0);

        kubernetesNamespaceSetupper.setupNamespace(Environment.PREPROD, Constants.NAMESPACE_NAME_PREPROD);

        //kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD, 30);

        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubePreprodServerPort(), Constants.SERVICE_PORT);

        addDisabledRolesIfMissing();
    }

    @SneakyThrows
    private void addDisabledRolesIfMissing() {
        try (Connection connection = DatabaseUtil.getConnection(platformProperties.getLocalDatabasePort(), "apphub_preprod")) {
            platformProperties.getProdDisabledRoles()
                .forEach(role -> DatabaseUtil.insertDisabledRoleIfNotPresent(connection, role));
        }
    }

    public void deployServices(List<String> serviceNames) {
        localStopProcess.stopAllServices();

        if (!minikubeBuildTask.installServices(serviceNames)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        //kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD, serviceNames, 15);

        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubePreprodServerPort(), Constants.SERVICE_PORT);

        log.info("Deployment finished.");
    }
}
