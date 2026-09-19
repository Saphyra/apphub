package com.github.saphyra.apphub.ci.service.vm.minikube;

import com.github.saphyra.apphub.ci.tool.KubernetesStopper;
import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeStopVmService {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;
    private final NamespaceNameProvider namespaceNameProvider;

    public void stop() {
        kubernetesPodScaler.scaleAll(namespaceNameProvider.getNamespaceName(), 0);

        kubernetesStopper.stopMinikube();
    }
}
