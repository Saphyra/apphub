package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesStopper;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionStopProcess {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;

    public void stopMinikube() {
        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        kubernetesStopper.stopMinikube();
    }
}
