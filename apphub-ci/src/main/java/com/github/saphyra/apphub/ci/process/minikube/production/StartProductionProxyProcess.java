package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubePodStartupWaiter;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartProductionProxyProcess {
    private final ProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final PortForwardTask portForwardTask;
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;

    public void startProductionProxy() {
        minikubePodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PRODUCTION);
        processKiller.killByPort(platformProperties.getMinikubeProdServerPort());

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeProdMainGatewayPort(), Constants.SERVICE_PORT);

        new LocalStartTask(servicePinger, platformProperties.getProductionProxy())
            .run();
    }
}
