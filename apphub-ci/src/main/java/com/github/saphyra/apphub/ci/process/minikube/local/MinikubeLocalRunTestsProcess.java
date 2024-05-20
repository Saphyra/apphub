package com.github.saphyra.apphub.ci.process.minikube.local;

import com.github.saphyra.apphub.ci.process.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.process.RunTestsTask;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeLocalRunTestsProcess {
    private final NamespaceNameProvider namespaceNameProvider;
    private final PortForwardTask portForwardTask;
    private final PlatformProperties platformProperties;
    private final ProcessKiller processKiller;
    private final ServicePinger servicePinger;
    private final IntegrationServerStarter integrationServerStarter;
    private final RunTestsTask runTestsTask;

    public void runTests() {
        try {
            log.info("");
            String namespaceName = namespaceNameProvider.getBranchName();

            portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeTestServerPort(), Constants.SERVICE_PORT);
            portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeTestDatabasePort(), Constants.POSTGRES_PORT);

            Exception exception = servicePinger.pingRemote(platformProperties.getMinikubeTestServerPort(), 10).orElse(null);
            if (!isNull(exception)) {
                throw exception;
            }
            log.info("Ping successful.");

            integrationServerStarter.start();

            runTestsTask.remoteRunTests("");

        } catch (Exception e) {
            log.error("Test run failed.", e);
        } finally {
            processKiller.killByPort(platformProperties.getMinikubeTestServerPort());
            processKiller.killByPort(platformProperties.getMinikubeTestDatabasePort());
        }
    }
}
