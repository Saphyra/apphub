package com.github.saphyra.apphub.ci.service.operations.production;

import com.github.saphyra.apphub.ci.tool.KubernetesPodStartupWaiter;
import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.tool.ProcessKiller;
import com.github.saphyra.apphub.ci.tool.ServiceStarter;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionProxyService {
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final ProcessKiller processKiller;
    private final PlatformProperties platformProperties;
    private final KubernetesPortForwarder kubernetesPortForwarder;
    private final ServiceStarter serviceStarter;
    private  final StartupIndicatorFactory startupIndicatorFactory;

    @SneakyThrows
    public void start() {
        kubernetesPodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PRODUCTION, 5);
        processKiller.killByPort(platformProperties.getMinikubeProdServerPort());

        Process process = new ProcessBuilder("cmd", "/c", "cd", "apphub-proxy", "&&", "mvn", "clean", "package")
            .inheritIO()
            .start();

        process.waitFor();

        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeProdMainGatewayPort(), Constants.SERVICE_PORT);


        serviceStarter.startService(platformProperties.getProductionProxy(), startupIndicatorFactory.noOpIndicator());
    }
}
