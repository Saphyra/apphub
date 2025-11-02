package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinikubeScaleDownMenuOption implements MenuOption {
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final NamespaceNameProvider namespaceNameProvider;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SCALE_DOWN;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.SCALE_DOWN;
    }

    @Override
    public boolean process() {
        minikubeScaleProcess.scale(namespaceNameProvider.getNamespaceName(), 0);

        return false;
    }
}
