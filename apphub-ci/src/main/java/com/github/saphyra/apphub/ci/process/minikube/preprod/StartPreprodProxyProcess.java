package com.github.saphyra.apphub.ci.process.minikube.preprod;

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
public class StartPreprodProxyProcess {
    private final DeprecatedProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final ServicePinger servicePinger;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final StartupIndicatorFactory startupIndicatorFactory;

    @SneakyThrows
    public void startPreprodProxy() {
        kubernetesPodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PREPROD, 5);
        processKiller.killByPort(platformProperties.getMinikubePreprodServerPort());

        Process process = new ProcessBuilder("cmd", "/c", "cd", "apphub-proxy", "&&", "mvn", "clean", "package")
            .inheritIO()
            .start();

        process.waitFor();

        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubePreprodMainGatewayPort(), Constants.SERVICE_PORT);

        new LocalStartTask(servicePinger, platformProperties.getPreprodProxy(), Constants.PROFILE_PREPROD, startupIndicatorFactory.noOpIndicator())
            .run();
    }
}
