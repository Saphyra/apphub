package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeLocalStopMenuOption implements MenuOption {
    private final MinikubeLocalStopProcess minikubeLocalStopProcess;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.STOP_VM;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_STOP;
    }

    @Override
    public boolean process() {
        minikubeLocalStopProcess.stopMinikube();

        return false;
    }
}
