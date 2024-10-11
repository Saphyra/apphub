package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.menu.minikube.MinikubeMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuMinikubeMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        return Menu.MAIN_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.MAIN_MENU_MINIKUBE;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE;
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(MinikubeMenu.class).enter();

        return false;
    }
}
