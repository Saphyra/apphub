package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.DeprecatedProcessKiller;
import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.tool.KubernetesPodStartupWaiter;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.tool.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartProductionProxyProcess {
    private final DeprecatedProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final StartupIndicatorFactory startupIndicatorFactory;

    @SneakyThrows
    public void startProductionProxy() {
        kubernetesPodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PRODUCTION, 5);
        processKiller.killByPort(platformProperties.getMinikubeProdServerPort());

        Process process = new ProcessBuilder("cmd", "/c", "cd", "apphub-proxy", "&&", "mvn", "clean", "package")
            .inheritIO()
            .start();

        process.waitFor();

        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeProdMainGatewayPort(), Constants.SERVICE_PORT);

        new LocalStartTask(servicePinger, platformProperties.getProductionProxy(), startupIndicatorFactory.noOpIndicator())
            .run();
    }
}
