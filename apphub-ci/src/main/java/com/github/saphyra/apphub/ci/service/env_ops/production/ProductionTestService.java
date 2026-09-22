package com.github.saphyra.apphub.ci.service.env_ops.production;

import com.github.saphyra.apphub.ci.service.env_ops.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodStartupWaiter;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.tool.ProcessKiller;
import com.github.saphyra.apphub.ci.tool.service.ServicePinger;
import com.github.saphyra.apphub.ci.tool.test.TestRunner;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionTestService {
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final PlatformProperties platformProperties;
    private final ProcessKiller processKiller;
    private final ServicePinger servicePinger;
    private final IntegrationServerStarter integrationServerStarter;
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final TestRunner testRunner;

    public void runTests(String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        try {
            kubernetesPodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PREPROD, 5);

            kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeTestServerPort(), Constants.SERVICE_PORT);

            servicePinger.pingRemote(platformProperties.getMinikubeTestServerPort(), 10)
                .ifPresentOrElse(
                    e -> {
                        throw new RuntimeException(e);
                    },
                    () -> log.info("Ping successful.")
                );

            integrationServerStarter.start();

            testRunner.runTests(
                Environment.PRODUCTION,
                testFilter,
                threadCount,
                platformProperties.getMinikubeTestServerPort(),
                platformProperties.getLocalDatabasePort(),
                platformProperties.getProdDatabaseName(),
                String.join(",", platformProperties.getProdDisabledTestGroups()),
                preCreatedDriverCount,
                true,
                false,
                Constants.NAMESPACE_NAME_PRODUCTION,
                retryCount,
                "0"
            );
        } finally {
            processKiller.killByPort(platformProperties.getMinikubeTestServerPort());
        }
    }
}
