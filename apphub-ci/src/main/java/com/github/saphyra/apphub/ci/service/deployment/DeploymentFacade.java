package com.github.saphyra.apphub.ci.service.deployment;

import com.github.saphyra.apphub.ci.service.deployment.local.LocalDeploymentService;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeploymentFacade {
    private final Services services;

    private final LocalDeploymentService localDeploymentService;

    public void buildAndDeploy(
        Environment environment,
        List<String> enabledServiceNames,
        boolean startUserDefinedServices,
        List<String> userDefinedServiceNames,
        int buildThreadCount,
        int startupCountLimit,
        boolean skipTests
    ) {
        switch (environment) {
            case LOCAL -> localDeploymentService.deploy(
                getServicesToStart(enabledServiceNames, startUserDefinedServices, userDefinedServiceNames),
                buildThreadCount,
                startupCountLimit,
                skipTests
            );
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    private List<Service> getServicesToStart(List<String> enabledServiceNames, boolean startUserDefinedServices, List<String> userDefinedServiceNames) {
        if (startUserDefinedServices) {
            return services.getServices()
                .stream()
                .filter(service -> userDefinedServiceNames.contains(service.getName()))
                .toList();

        } else {
            return services.getServices()
                .stream()
                .filter(service -> !service.getOptional() || enabledServiceNames.contains(service.getName()))
                .toList();
        }
    }
}
