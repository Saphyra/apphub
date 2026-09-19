package com.github.saphyra.apphub.ci.service.env_ops.minikube;

import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.tool.ServiceBuilder;
import com.github.saphyra.apphub.ci.tool.ServiceStopper;
import com.github.saphyra.apphub.ci.value.BuildCommand;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.DockerTag;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeDeploymentService {
    private final ServiceStopper serviceStopper;
    private final ServiceBuilder serviceBuilder;
    private final NamespaceNameProvider namespaceNameProvider;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final PlatformProperties platformProperties;
    private final KubernetesPortForwarder portForwarder;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final KubernetesPodScaler kubernetesPodScaler;

    public void deploy(boolean startUserDefinedServices, List<Service> services, int buildThreadCount, int startupCountLimit, boolean skipTests) {
        serviceStopper.stopLocalEnv();

        serviceBuilder.build(BuildCommand.INSTALL, services, buildThreadCount, skipTests);

        String namespaceName = namespaceNameProvider.getNamespaceName();

        if (!startUserDefinedServices) {
            serviceBuilder.buildFrontend(DockerTag.LATEST);
            kubernetesPodScaler.scaleAll(namespaceName, 0);
        }

        kubernetesNamespaceSetupper.setupNamespace(Environment.MINIKUBE, namespaceName);
        kubernetesNamespaceSetupper.deployPostgres(namespaceName);
        kubernetesNamespaceSetupper.deployDynamoDb(namespaceName);

        services = Stream.concat(services.stream(), Stream.of(Services.FRONTEND))
            .toList();

        kubernetesServiceDeployer.deploy(namespaceName, Constants.DIR_NAME_DEVELOP, services, 15, startupCountLimit);

        portForwarder.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        portForwarder.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
        portForwarder.portForward(namespaceName, Constants.SERVICE_NAME_DYNAMO_DB, platformProperties.getMinikubeDynamoDbPort(), platformProperties.getLocalDynamoDbPort());

        log.info("Deployment finished.");
    }
}
