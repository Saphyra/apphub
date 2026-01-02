package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubePodStartupWaiter;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartProductionProxyProcess {
    private final ProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final PortForwardTask portForwardTask;
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;
    private final StartupIndicatorFactory startupIndicatorFactory;

    @SneakyThrows
    public void startProductionProxy() {
        minikubePodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PRODUCTION, 5);
        processKiller.killByPort(platformProperties.getMinikubeProdServerPort());

        Process process = new ProcessBuilder("cmd", "/c", "cd", "apphub-proxy", "&&", "mvn", "clean", "package")
            .inheritIO()
            .start();

        process.waitFor();

        portForwardTask.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeProdMainGatewayPort(), Constants.SERVICE_PORT);

        LocalStartTask.builder()
            .servicePinger(servicePinger)
            .service(platformProperties.getProductionProxy())
            .startupIndicator(startupIndicatorFactory.noOpIndicator())
            .build()
            .run();
    }
}
