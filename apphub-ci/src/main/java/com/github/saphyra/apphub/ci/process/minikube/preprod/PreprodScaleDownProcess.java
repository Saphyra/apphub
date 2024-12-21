package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodScaleDownProcess {
    private final MinikubeScaleProcess minikubeScaleProcess;

    public void scaleDown() {
        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PREPROD, 0);
    }
}
