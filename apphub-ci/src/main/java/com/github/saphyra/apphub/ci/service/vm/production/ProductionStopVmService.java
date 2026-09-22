package com.github.saphyra.apphub.ci.service.vm.production;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.tool.KubernetesStopper;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO merge stopVmServices and accept namespaceName from method parameter
public class ProductionStopVmService {
    private final KubernetesPodScaler kubernetesPodScaler;
    private final KubernetesStopper kubernetesStopper;

    public void stop() {
        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        kubernetesStopper.stopMinikube();
    }
}
