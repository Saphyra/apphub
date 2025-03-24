package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.process.RunTestsTask;
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
public class PreprodRunTestsProcess {
    private final PortForwardTask portForwardTask;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final IntegrationServerStarter integrationServerStarter;
    private final RunTestsTask runTestsTask;

    public void runTests() {
        runTests("");
    }

    public void runTests(String testGroups) {
        try {
            log.info("");

            portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeTestServerPort(), Constants.SERVICE_PORT);

            Exception exception = servicePinger.pingRemote(platformProperties.getMinikubeTestServerPort(), 10).orElse(null);
            if (!isNull(exception)) {
                throw exception;
            }
            log.info("Ping successful.");

            integrationServerStarter.start();

            runTestsTask.preprodRunTests(testGroups);
        } catch (Exception e) {
            log.error("Test run failed.", e);
        }
    }
}
