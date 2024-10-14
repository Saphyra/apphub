package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.MinikubeEditConfigurationMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeEditConfigurationMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.EDIT_CONFIGURATION;
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(MinikubeEditConfigurationMenu.class).enter();

        return false;
    }
}
