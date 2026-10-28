package com.github.saphyra.apphub.ci.service.vm.production;

import com.github.saphyra.apphub.ci.dao.property.PropertyDao;
import com.github.saphyra.apphub.ci.dao.property.PropertyName;
import com.github.saphyra.apphub.ci.service.operations.production.ProductionProxyService;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesNamespaceSetupper;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesServiceDeployer;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesStarter;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import com.github.saphyra.apphub.ci.value.Technology;
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
    private final KubernetesNamespaceSetupper kubernetesNamespaceSetupper;
    private final PropertyDao propertyDao;
    private final DefaultProperties defaultProperties;

    public void start() {
        kubernetesStarter.start();

        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        kubernetesNamespaceSetupper.setupNamespace(Environment.PRODUCTION, Constants.NAMESPACE_NAME_PRODUCTION, Technology.RABBITMQ);

        List<Service> servicesToStart = Stream.concat(services.getServices().stream(), Stream.of(Services.FRONTEND))
            .toList();

        int startupCountLimit = propertyDao.getProperty(PropertyName.STARTUP_COUNT_LIMIT)
            .map(Integer::parseInt)
            .orElse(defaultProperties.getServiceStartupCountLimit());

        kubernetesServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION, servicesToStart, 60, startupCountLimit);

        productionProxyService.start();
    }
}
