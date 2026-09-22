package com.github.saphyra.apphub.ci.service.vm;

import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesStopper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StopVmService {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;

    public void stop(String namespaceName) {
        kubernetesPodScaler.scaleAll(namespaceName, 0);

        kubernetesStopper.stopMinikube();
    }
}
