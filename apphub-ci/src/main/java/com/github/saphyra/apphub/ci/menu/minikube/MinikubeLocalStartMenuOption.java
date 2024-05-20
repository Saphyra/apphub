package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeLocalStartMenuOption implements MinikubeMenuOption {
    private final MinikubeStartProcess minikubeStartProcess;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_START;
    }

    @Override
    public boolean process() {
        minikubeStartProcess.startMinikube();

        return false;
    }
}
