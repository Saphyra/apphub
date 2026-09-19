package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.tool.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodScaleDownProcess {
    private final KubernetesPodScaler kubernetesPodScaler;

    public void scaleDown() {
        kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PREPROD, 0);
    }
}
