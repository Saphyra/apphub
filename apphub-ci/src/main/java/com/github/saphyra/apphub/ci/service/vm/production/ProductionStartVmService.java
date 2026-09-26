package com.github.saphyra.apphub.ci.service.vm.production;

import com.github.saphyra.apphub.ci.service.operations.production.ProductionProxyService;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesStarter;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductionStartVmService {
    private final KubernetesStarter kubernetesStarter;
    private final KubernetesServiceDeployer kubernetesServiceDeployer;
    private final KubernetesPodScaler kubernetesPodScaler;
    private final ProductionProxyService productionProxyService;
    private final Services services;

    public void start() {
        kubernetesStarter.start();

        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        List<Service> servicesToStart = Stream.concat(services.getServices().stream(), Stream.of(Services.FRONTEND))
            .toList();

        kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, servicesToStart, 60, 2);

        productionProxyService.start();
    }
}
