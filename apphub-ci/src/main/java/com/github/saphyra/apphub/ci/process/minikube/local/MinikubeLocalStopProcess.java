package com.github.saphyra.apphub.ci.process.minikube.local;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeLocalStopProcess {
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeStopProcess minikubeStopProcess;
    private final NamespaceNameProvider namespaceNameProvider;

    public void stopMinikube() {
        minikubeScaleProcess.scale(namespaceNameProvider.getNamespaceName(), 0);

        minikubeStopProcess.stopMinikube();
    }
}
