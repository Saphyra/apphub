package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.tool.KubernetesStarter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStartProcess {
    private final KubernetesStarter kubernetesStarter;

    public void startServer() {
        kubernetesStarter.start();
    }
}
