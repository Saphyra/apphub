package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeNamespaceSetupTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.utils.DatabaseUtil;
import com.github.saphyra.apphub.ci.value.Constants;
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
public class PreprodReleaseProcess {
    private final MinikubeBuildTask minikubeBuildTask;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeNamespaceSetupTask minikubeNamespaceSetupTask;
    private final LocalStopProcess localStopProcess;
    private final PortForwardTask portForwardTask;
    private final PlatformProperties platformProperties;

    public void release() {
        localStopProcess.stopServices();

        if (!minikubeBuildTask.installServices()) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PREPROD, 0);

        minikubeNamespaceSetupTask.setupNamespace(Constants.NAMESPACE_NAME_PREPROD);

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD, 30);

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);

        addDisabledRolesIfMissing();
    }

    @SneakyThrows
    private void addDisabledRolesIfMissing() {
        try (Connection connection = DatabaseUtil.getConnection(platformProperties.getMinikubeDatabasePort(), "postgres")) {
            platformProperties.getProdDisabledRoles()
                .forEach(role -> DatabaseUtil.insertDisabledRoleIfNotPresent(connection, role));
        }
    }

    public void deployServices(List<String> serviceNames) {
        localStopProcess.stopServices();

        if (!minikubeBuildTask.installServices(serviceNames)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_DEVELOP, serviceNames, 15);

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);

        log.info("Deployment finished.");
    }
}
