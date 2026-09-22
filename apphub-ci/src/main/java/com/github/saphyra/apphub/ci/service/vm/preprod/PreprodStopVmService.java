package com.github.saphyra.apphub.ci.service.vm.preprod;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesStopper;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStopVmService {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;

    public void stop() {
        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PREPROD, 0);

        kubernetesStopper.stopMinikube();
    }
}
