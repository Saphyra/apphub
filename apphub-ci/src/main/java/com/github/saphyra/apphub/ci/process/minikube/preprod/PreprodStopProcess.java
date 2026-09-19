package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesStopper;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStopProcess {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;

    public void stopMinikube() {
        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PREPROD, 0);

        kubernetesStopper.stopMinikube();
    }
}
