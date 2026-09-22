package com.github.saphyra.apphub.ci.service.env_ops.preprod;

import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.tool.ServiceBuilder;
import com.github.saphyra.apphub.ci.tool.ServiceStopper;
import com.github.saphyra.apphub.ci.util.DatabaseUtil;
import com.github.saphyra.apphub.ci.value.BuildCommand;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.DockerTag;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PreprodDeploymentService {
    private final ServiceStopper serviceStopper;
    private final ServiceBuilder serviceBuilder;
    private final NamespaceNameProvider namespaceNameProvider;
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final PlatformProperties platformProperties;

    public void deploy(boolean startUserDefinedServices, List<Service> services, int buildThreadCount, int startupCountLimit, boolean skipTests) {
        serviceStopper.stopLocalEnv();

        serviceBuilder.build(BuildCommand.INSTALL, services, buildThreadCount, skipTests);

        String namespaceName = namespaceNameProvider.getNamespaceName();

        if (!startUserDefinedServices) {
            serviceBuilder.buildFrontend(DockerTag.LATEST);
            kubernetesPodScaler.scaleAll(namespaceName, 0);

            services = Stream.concat(services.stream(), Stream.of(Services.FRONTEND))
                .toList();

            kubernetesNamespaceSetupper.setupNamespace(Environment.PREPROD, namespaceName);
        }

        kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD, services, 30, startupCountLimit);

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
}
