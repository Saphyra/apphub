package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeBuildTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeNamespaceSetupTask;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionReleaseProcess {
    private final ValidatingInputReader validatingInputReader;
    private final MinikubeBuildTask minikubeBuildTask;
    private final MinikubeServiceDeployer minikubeServiceDeployer;
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeNamespaceSetupTask minikubeNamespaceSetupTask;
    private final LocalStopProcess localStopProcess;
    private final StartProductionProxyProcess startProductionProxyProcess;

    public void release() {
        String username = validatingInputReader.getInput(
            LocalizedText.DOCKER_HUB_USERNAME,
            input -> {
                if (input.isBlank()) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_BLANK);
                }

                return Optional.empty();
            }
        );
        String password = validatingInputReader.getInput(
            LocalizedText.DOCKER_HUB_PASSWORD,
            input -> {
                if (input.isBlank()) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_BLANK);
                }

                return Optional.empty();
            }
        );

        localStopProcess.stopServices();

        if (!minikubeBuildTask.deployServices(username, password)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        minikubeNamespaceSetupTask.createNamespace(Constants.NAMESPACE_NAME_PRODUCTION);

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PRODUCTION, Constants.DIR_NAME_PRODUCTION);

        startProductionProxyProcess.startProductionProxy();
    }
}
