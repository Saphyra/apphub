package com.github.saphyra.apphub.ci.process.minikube.preprod;

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
public class StartPreprodProxyProcess {
    private final ProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final PortForwardTask portForwardTask;
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;

    public void startPreprodProxy() {
        minikubePodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PREPROD);
        processKiller.killByPort(platformProperties.getMinikubePreprodServerPort());

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubePreprodMainGatewayPort(), Constants.SERVICE_PORT);

        LocalStartTask.builder()
            .servicePinger(servicePinger)
            .service(platformProperties.getPreprodProxy())
            .activeProfiles(Constants.PROFILE_PREPROD)
            .protocol(Constants.PROTOCOL_SECURE)
            .build()
            .run();
    }
}
