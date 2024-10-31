package com.github.saphyra.apphub.ci.process.minikube.preprod;

import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreprodStartProcess {
    private final MinikubeStartProcess minikubeStartProcess;

    public void startServer() {
        minikubeStartProcess.startMinikube();
    }
}
