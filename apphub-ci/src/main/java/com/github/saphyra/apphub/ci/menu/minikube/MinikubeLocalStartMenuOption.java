package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeLocalStartMenuOption implements MenuOption {
    private final MinikubeStartProcess minikubeStartProcess;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.START_VM;
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
