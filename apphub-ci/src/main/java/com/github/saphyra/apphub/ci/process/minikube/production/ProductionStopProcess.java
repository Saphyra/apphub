package com.github.saphyra.apphub.ci.process.minikube.production;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStopProcess;
import com.github.saphyra.apphub.ci.value.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionStopProcess {
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeStopProcess minikubeStopProcess;

    public void stopMinikube() {
        minikubeScaleProcess.scale(Constants.NAMESPACE_NAME_PRODUCTION, 0);

        minikubeStopProcess.stopMinikube();
    }
}
