package com.github.saphyra.apphub.ci.service.operations.minikube;

import com.github.saphyra.apphub.ci.tool.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.tool.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubePortForwardService {
    private final NamespaceNameProvider namespaceNameProvider;
    private final PlatformProperties platformProperties;
    private final KubernetesPortForwarder kubernetesPortForwarder;

    public void portForward() {
        String namespaceName = namespaceNameProvider.getNamespaceName();

        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
        kubernetesPortForwarder.portForward(namespaceName, Constants.SERVICE_NAME_DYNAMO_DB, platformProperties.getMinikubeDynamoDbPort(), platformProperties.getLocalDynamoDbPort());
    }
}
