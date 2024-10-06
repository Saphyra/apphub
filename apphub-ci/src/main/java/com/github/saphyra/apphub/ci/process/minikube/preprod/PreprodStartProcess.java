package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeServiceDeployer;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStartProcess {
    private final MinikubeStartProcess minikubeStartProcess;
    private final MinikubeServiceDeployer minikubeServiceDeployer;

    public void startServer() {
        minikubeStartProcess.startMinikube();

        minikubeServiceDeployer.deploy(Constants.NAMESPACE_NAME_PREPROD, Constants.DIR_NAME_PREPROD);
    }
}
