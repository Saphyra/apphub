package com.github.saphyra.apphub.ci.service.env_ops.minikube;

import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.kubernetes.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeStopService {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final NamespaceNameProvider namespaceNameProvider;

    public void stopServices(boolean stopUserDefinedServices, List<Service> services) {
        String namespace = namespaceNameProvider.getNamespaceName();

        if (stopUserDefinedServices) {
            kubernetesPodScaler.scale(services, namespace, 0);
        }else{
            kubernetesPodScaler.scaleAll(namespace, 0);
        }
    }
}
