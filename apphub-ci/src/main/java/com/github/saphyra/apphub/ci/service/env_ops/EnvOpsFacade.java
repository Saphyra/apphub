package com.github.saphyra.apphub.ci.service.env_ops;

import com.github.saphyra.apphub.ci.service.env_ops.local.LocalDeploymentService;
import com.github.saphyra.apphub.ci.service.env_ops.local.LocalStopService;
import com.github.saphyra.apphub.ci.service.env_ops.local.LocalTestService;
import com.github.saphyra.apphub.ci.service.env_ops.minikube.MinikubeDeploymentService;
import com.github.saphyra.apphub.ci.service.env_ops.minikube.MinikubeStopService;
import com.github.saphyra.apphub.ci.service.env_ops.minikube.MinikubeTestService;
import com.github.saphyra.apphub.ci.service.env_ops.preprod.PreprodDeploymentService;
import com.github.saphyra.apphub.ci.service.env_ops.preprod.PreprodStopService;
import com.github.saphyra.apphub.ci.service.env_ops.preprod.PreprodTestService;
import com.github.saphyra.apphub.ci.service.env_ops.production.ProductionDeploymentService;
import com.github.saphyra.apphub.ci.service.env_ops.production.ProductionTestService;
import com.github.saphyra.apphub.ci.value.Action;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnvOpsFacade {
    private final Services services;
    private final LocalTestService localTestService;
    private final LocalDeploymentService localDeploymentService;
    private final LocalStopService localStopService;
    private final MinikubeDeploymentService minikubeDeploymentService;
    private final MinikubeTestService minikubeTestService;
    private final MinikubeStopService minikubeStopService;
    private final PreprodDeploymentService preprodDeploymentService;
    private final PreprodTestService preprodTestService;
    private final PreprodStopService preprodStopService;
    private final ProductionDeploymentService productionDeploymentService;
    private final ProductionTestService productionTestService;

    public void buildAndDeploy(
        Action action,
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
                action,
                startUserDefinedServices,
                getServicesToStart(enabledServiceNames, startUserDefinedServices, userDefinedServiceNames),
                buildThreadCount,
                startupCountLimit,
                skipTests
            );
            case MINIKUBE -> minikubeDeploymentService.deploy(
                action,
                startUserDefinedServices,
                getServicesToStart(enabledServiceNames, startUserDefinedServices, userDefinedServiceNames),
                buildThreadCount,
                startupCountLimit,
                skipTests
            );
            case PREPROD -> preprodDeploymentService.deploy(
                action,
                startUserDefinedServices,
                getServicesToStart(enabledServiceNames, startUserDefinedServices, userDefinedServiceNames),
                buildThreadCount,
                startupCountLimit,
                skipTests
            );
            case PRODUCTION -> productionDeploymentService.deploy(
                action,
                startUserDefinedServices,
                getServicesToStart(enabledServiceNames, startUserDefinedServices, userDefinedServiceNames),
                buildThreadCount,
                startupCountLimit,
                skipTests
            );
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    public void runTests(Environment environment, String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        switch (environment) {
            case LOCAL -> localTestService.runTests(testFilter, threadCount, preCreatedDriverCount, retryCount);
            case MINIKUBE -> minikubeTestService.runTests(testFilter, threadCount, preCreatedDriverCount, retryCount);
            case PREPROD -> preprodTestService.runTests(testFilter, threadCount, preCreatedDriverCount, retryCount);
            case PRODUCTION -> productionTestService.runTests(testFilter, threadCount, preCreatedDriverCount, retryCount);
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    public void stop(Environment environment, boolean stopUserDefinedServices, List<String> userDefinedServiceNames) {
        switch (environment) {
            case LOCAL -> localStopService.stopServices(stopUserDefinedServices, getServicesToStop(stopUserDefinedServices, userDefinedServiceNames));
            case MINIKUBE -> minikubeStopService.stopServices(stopUserDefinedServices, getServicesToStop(stopUserDefinedServices, userDefinedServiceNames));
            case PREPROD -> preprodStopService.stopServices(stopUserDefinedServices, getServicesToStop(stopUserDefinedServices, userDefinedServiceNames));
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    private List<Service> getServicesToStop(boolean stopUserDefinedServices, List<String> userDefinedServiceNames) {
        if (stopUserDefinedServices) {
            return services.getServices()
                .stream()
                .filter(service -> userDefinedServiceNames.contains(service.getName()))
                .toList();

        } else {
            return services.getServices();
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
