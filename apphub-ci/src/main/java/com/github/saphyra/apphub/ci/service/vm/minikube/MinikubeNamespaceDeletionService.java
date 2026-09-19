package com.github.saphyra.apphub.ci.service.vm.minikube;

import com.github.saphyra.apphub.ci.tool.KubernetesNamespaceDeleter;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeNamespaceDeletionService {
    private final NamespaceNameProvider namespaceNameProvider;
    private final KubernetesNamespaceDeleter kubernetesNamespaceDeleter;

    public void delete() {
        kubernetesNamespaceDeleter.deleteNamespace(namespaceNameProvider.getNamespaceName());
    }
}
