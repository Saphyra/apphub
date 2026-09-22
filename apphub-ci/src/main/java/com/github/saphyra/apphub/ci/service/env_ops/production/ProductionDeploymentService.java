package com.github.saphyra.apphub.ci.service.env_ops.production;

import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.ServiceBuilder;
import com.github.saphyra.apphub.ci.tool.ServiceStopper;
import com.github.saphyra.apphub.ci.value.BuildCommand;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.DockerTag;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductionDeploymentService {
    private final ServiceStopper serviceStopper;
    private final ServiceBuilder serviceBuilder;
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;

    public void deploy(boolean startUserDefinedServices, List<Service> services, int buildThreadCount, int startupCountLimit, boolean skipTests) {
        serviceStopper.stopLocalEnv();

        serviceBuilder.build(BuildCommand.DEPLOY, services, buildThreadCount, skipTests);

        if (!startUserDefinedServices) {
            serviceBuilder.buildFrontend(DockerTag.LATEST);
            kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

            services = Stream.concat(services.stream(), Stream.of(Services.FRONTEND))
                .toList();

            kubernetesNamespaceSetupper.setupNamespace(Environment.PRODUCTION, Constants.NAMESPACE_NAME_PRODUCTION);
        }

        kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, services, 30, startupCountLimit);
    }
}
