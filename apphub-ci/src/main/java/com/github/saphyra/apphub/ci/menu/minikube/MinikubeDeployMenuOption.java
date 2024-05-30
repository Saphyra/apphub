package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalDeployProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeDeployMenuOption implements MenuOption {
    private final MinikubeLocalDeployProcess minikubeLocalDeployProcess;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_DEPLOY;
    }

    @Override
    public boolean process() {
        minikubeLocalDeployProcess.deploy();

        return false;
    }
}
