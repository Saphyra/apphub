package com.github.saphyra.apphub.ci.service.operations.production;

import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPortForwarder;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionPortForwardService {
    private final PlatformProperties platformProperties;
    private final KubernetesPortForwarder kubernetesPortForwarder;

    public void portForward() {
        kubernetesPortForwarder.portForward(Constants.NAMESPACE_NAME_PRODUCTION, Constants.SERVICE_NAME_RABBIT_MQ, platformProperties.getLocalRabbitMqAdminPort(), platformProperties.getRabbitMqAdminPort());
    }
}
